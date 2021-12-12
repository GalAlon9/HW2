package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU cpu;
    int tick = 0;
    public CPUService(String name , CPU cpu) {
        super("Change_This_Name");
        // TODO Implement this
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> terminate());

        subscribeBroadcast(TickBroadcast.class , tickBroadcast -> {
            tick = tickBroadcast.get();
            cpu.updateTick(tick);
            // TODO: add more actions following tick update
        });

    }
}
