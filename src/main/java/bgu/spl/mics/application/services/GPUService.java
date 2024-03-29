package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private final GPU gpu;
    private int tick = 0;
    private final ConcurrentHashMap<Model, Event> trainMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Model, Event> testMap = new ConcurrentHashMap<>();


    public GPUService(GPU gpu) {
        super("GPU service");
        // TODO Implement this
        this.gpu = gpu;
        gpu.setGpuService(this);
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
            System.out.println("gpu service terminated");
        });

        subscribeEvent(TrainModelEvent.class, modelEvent -> {
            Model model = modelEvent.getModel();
            gpu.addTrainModel(model);
            trainMap.put(model, modelEvent);

        });
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            tick = tickBroadcast.get();
            gpu.updateTick(tick);
        });

        subscribeEvent(TestModelEvent.class, modelEvent -> {
            Model model = modelEvent.getModel();
            gpu.addTestModel(model);
            testMap.put(model, modelEvent);
        });

        // wait for all microServices to subscribe
        CRMSRunner.countDown.countDown();

    }

    public void completeEvent(Model model) {
        if (trainMap.containsKey(model)) {
            Event trainEvent = trainMap.get(model);
            if (trainEvent.getClass().equals(TrainModelEvent.class)) {
                model.setStatus(Model.Status.Trained);
                complete(trainEvent, model.getStatus());
                System.out.println("Done training " + model.getName() + "at tick : "+ tick);
                trainMap.remove(model);
            }
        } else if (testMap.containsKey(model)) {
            { // testModel event
                Event testEvent = testMap.get(model);
                model.setStatus(Model.Status.Tested);
                complete(testEvent, model.getResult());
                System.out.println("done testing " + model.getName() + " with result: " + model.getResult().toString());
                testMap.remove(model);
            }
        }


    }


}
