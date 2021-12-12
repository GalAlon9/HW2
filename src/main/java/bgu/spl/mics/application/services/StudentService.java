package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    public StudentService(String name, Student student) {
        super("Change_This_Name");
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> terminate());
        subscribeBroadcast(PublishConfrenceBroadcast.class,c -> {});
        for(Model model:this.student.getModels()){
            trainMap.put(model,sendEvent(new TrainModelEvent(model)));
        }
        while(!trainMap.isEmpty()) {
            for (Model model : trainMap.keySet()) {
                if (trainMap.get(model).isDone()) {
                    testMap.put(model, sendEvent(new TestModelEvent(model)));
                    trainMap.remove(model);
                }
            }
        }
    }
}
