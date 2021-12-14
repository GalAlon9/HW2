package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.HashMap;

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
    private Student student;
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
        subscribeBroadcast(TerminateBroadcast.class, t -> terminate());

        subscribeBroadcast(PublishConferenceBroadcast.class, c -> {
            int published = c.getPublished(student);
            student.increasePublications(published);
            int paperRead = c.getRead(student);
            student.increasePapersRead(paperRead);
        });
// todo: maybe prioritize testModel in the queue

        for(Model model : student.getModels()){
            Future<Model.Status> trainFuture = trainModel(model);
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

    }
    private Future trainModel(Model model){
        return sendEvent(new TrainModelEvent(model));
    }
    private Future testModel(Model model){
        return sendEvent(new TestModelEvent(model));
    }
    private Future publishResult(Model model){
        return sendEvent(new PublishResultsEvent(model));
    }
}
