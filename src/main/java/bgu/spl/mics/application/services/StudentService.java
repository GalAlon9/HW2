package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.OutputResults.ModelRes;
import bgu.spl.mics.application.objects.OutputResults.OutputJson;
import bgu.spl.mics.application.objects.OutputResults.StudentRes;

import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private String name;
    private final Student student;
    private int tick = 0;
    private Model currModel = null;
    private int currModelIndex =0;

    public StudentService(Student student) {
        super("student service");
        this.student = student;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            collectData();
            terminate();
            System.out.println("student " + student.getName() + " terminated");
        });

        subscribeBroadcast(PublishConferenceBroadcast.class, c -> {
            int published = c.getPublished(student);
            student.increasePublications(published);
            int paperRead = c.getRead(student);
            student.increasePapersRead(paperRead);
        });

        subscribeBroadcast(TickBroadcast.class, c -> {
            act();
        });

        // wait for all microServices to subscribe
        CRMSRunner.countDown.countDown();
        try {
            CRMSRunner.countDown.await();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        setNextModel();
        act();


    }

    private void act() {
        //  send models to train -> test -> publish
        if (currModel == null) {
            return;
        }
        if ((currModel.getStatus().equals(Model.Status.Tested))) {
            if (currModel.getResult().equals(Model.Result.Good)) {
                publishResult();
            }
            setNextModel();
        } else if (currModel.getStatus().equals(Model.Status.Trained)) {
            currModel.setStatus(Model.Status.Testing);
            testModel();
        } else if (currModel.getStatus().equals(Model.Status.PreTrained)){
            currModel.setStatus(Model.Status.Training);
            trainModel();
        }
    }

//    }
    private void setNextModel() {
        if (currModelIndex < student.getModels().size()) {
            currModel = student.getModels().get(currModelIndex);
            currModelIndex++;
            System.out.println("student : "+student.getName() +"next model = " + currModel.getName() + " curr model index = " + (currModelIndex-1));
        } else currModel = null;

    }

    private Future trainModel() {
        System.out.println("------------sending trainig model event: "+ currModel.getName());
        return sendEvent(new TrainModelEvent(currModel));

    }

    private Future testModel() {
        System.out.println("------------sending testing model event: "+ currModel.getName());
        return sendEvent(new TestModelEvent(currModel));
    }

    private void publishResult() {
        System.out.println("------------sending publish model event: "+ currModel.getName());
        sendEvent(new PublishResultsEvent(currModel));
    }

    private void collectData() {
        // after the run collect results by student
        LinkedList<ModelRes> modelResLinkedList = new LinkedList<>();
        for (Model model : student.getModels()) {
            if (model.getStatus() == Model.Status.Tested || model.getStatus() == Model.Status.Trained) {
                ModelRes modelRes = new ModelRes(model.getName(), model.getData(), model.statusToString(), model.resultToString());
                modelResLinkedList.add(modelRes);
            }
        }
        StudentRes studentRes = new StudentRes(this.student.getName(), this.student.getDepartment(), this.student.statusToString(),
                this.student.getPublications(), this.student.getPapersRead(), modelResLinkedList);
        OutputJson.getInstance().addStudentRes(studentRes);
    }
}

