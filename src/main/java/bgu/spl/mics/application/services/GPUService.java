package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    GPU gpu;
    int tick;
    public GPUService(GPU gpu) {
        super("GPU service");
        // TODO Implement this
        this.gpu = gpu;
        tick = 0;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> terminate());

//        subscribeBroadcast(TrainModelEvent.class, model -> );
    }

    private Model.Result Result(Model model){
        Random rnd = new Random();
        double prob = rnd.nextDouble();
        if(model.getStudent().getStatus().equals(Student.Degree.MSc)){
            return prob <= 0.6 ? Model.Result.Good : Model.Result.Bad;
        }
        else return prob <= 0.8 ? Model.Result.Good : Model.Result.Bad;
    }
}
