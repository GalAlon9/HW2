package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.CPU;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU cpu;
    int tick;
    public CPUService(CPU cpu) {
        super("CPU service");
        this.cpu = cpu;
        tick = 0;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
            System.out.println("cpu service terminated");
        });

        subscribeBroadcast(TickBroadcast.class , tickBroadcast -> {
            tick = tickBroadcast.get();
            cpu.updateTick(tick);
        });

        // wait for all microServices to subscribe
        CRMSRunner.countDown.countDown();

    }
}
