package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.OutputResults.ModelRes;
import bgu.spl.mics.application.objects.OutputResults.OutputJson;
import bgu.spl.mics.application.objects.OutputResults.StudentRes;

import java.util.HashMap;
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
//    private HashMap<Model, Future> trainMap;
//    private HashMap<Model, Future> testMap;
//    private HashMap<Model, Future> publishedMap;

    public StudentService(Student student) {
        super("student service");
        this.student = student;
//        this.trainMap = new HashMap<>();
//        this.testMap = new HashMap<>();
//        this.publishedMap = new HashMap<>();
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
            System.out.println("student " + student.getName() + " terminated");
        });

        subscribeBroadcast(PublishConferenceBroadcast.class, c -> {
            int published = c.getPublished(student);
            student.increasePublications(published);
            int paperRead = c.getRead(student);
            student.increasePapersRead(paperRead);
        });
        // send models to train -> test -> publish
        for(Model model : student.getModels()){
            Future<Model.Status> trainFuture = trainModel(model);
            if(trainFuture == null){
                int x = 2;
            }
            if(trainFuture != null) { // todo: fix this line
                Model.Status status = trainFuture.get();
                if(status.equals(Model.Status.Trained)){
                    Future<Model.Result> testFuture = testModel(model);
                    if(testFuture.get().equals(Model.Result.Good)){
                        publishResult(model);
                    }
                }
            }
        }
        //after the run collect results by student
        LinkedList<ModelRes> modelResLinkedList = new LinkedList<>();
        for(Model model:student.getModels()){
            if(model.getStatus()== Model.Status.Tested||model.getStatus()== Model.Status.Trained){
                ModelRes modelRes = new ModelRes(model.getName(), model.getData(), model.statusToString(), model.resultToString());
                modelResLinkedList.add(modelRes);
            }
        }
        StudentRes studentRes = new StudentRes(this.student.getName(),this.student.getDepartment(),this.student.statusToString(),
                this.student.getPublications(),this.student.getPapersRead(),modelResLinkedList);
        OutputJson.getInstance().addStudentRes(studentRes);


    }
    private Future trainModel(Model model){
        return sendEvent(new TrainModelEvent(model));
    }
    private Future testModel(Model model){
        return sendEvent(new TestModelEvent(model));
    }
    private void publishResult(Model model){
        sendEvent(new PublishResultsEvent(model));
    }
}
