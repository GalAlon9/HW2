package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.*;
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
    private int tick;
    private final ConcurrentHashMap<Model, Event> modelMap;

    public GPUService(GPU gpu) {
        super("GPU service");
        // TODO Implement this
        this.gpu = gpu;
        gpu.setGpuService(this);
        tick = 0;
        modelMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            // release all un-complete models
//            for (Model model : modelMap.keySet()) {
//                completeEvent(model);
//            }
            terminate();
            System.out.println("gpu service terminated");
        });

        subscribeEvent(TrainModelEvent.class, modelEvent -> {
            Model model = modelEvent.getModel();
            gpu.addTrainModel(model);
            modelMap.put(model, modelEvent);

        });
        subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            tick = tickBroadcast.get();
            gpu.updateTick(tick);
        });

        subscribeEvent(TestModelEvent.class, modelEvent -> {
            Model model = modelEvent.getModel();
            gpu.addTestModel(model);
            modelMap.put(model, modelEvent);
        });

        // wait for all microServices to subscribe
        CRMSRunner.countDown.countDown();

    }

    public void completeEvent(Model model) {
        Event event = modelMap.get(model);
        if (event.getClass() == TrainModelEvent.class) {
            complete(event, model.getStatus());
            System.out.println("done training " + model.getName() + " with status: " + model.getStatus().toString());
        } else { // testModel event
            complete(event, model.getResult());
            System.out.println("done testing " + model.getName() + " with result: " +model.getResult().toString());

        }
        modelMap.remove(model);


    }


}
