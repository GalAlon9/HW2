package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.OutputResults.OutputJson;

import java.sql.Time;
import java.time.Clock;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    int currTick;
    int duration;
    int speed;
    Timer timer;

    public TimeService(int speed, int duration) {
        super("TimeService");
        currTick = 0;
        this.speed = speed;
        this.duration = duration;
    }


    @Override
    protected void initialize() {

        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
        });
        // send tick broadcast every tick, and send terminate broadcast when time is over


        // wait for all microServices to subscribe
        CRMSRunner.countDown.countDown();
        try {
            CRMSRunner.countDown.await();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        act();
    }

    private void act() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                currTick++;
                if (currTick < duration) {
                    if(currTick % 500 == 0){
                        System.out.println("tick = " + currTick);
                    }
                    sendBroadcast(new TickBroadcast(currTick));
                }
                // reached end of the duration - terminate all processes, and collect time results to the output
                else {
                    sendBroadcast(new TerminateBroadcast());
                    timer.cancel();
                    System.out.println("time service terminated");

                    OutputJson.getInstance().setCPUTime(Cluster.getInstance().getCpuTime());
                    OutputJson.getInstance().setGPUTime(Cluster.getInstance().getGpuTime());
                    OutputJson.getInstance().setBatches(Cluster.getInstance().getProcessedData());
                }
            }
        }, speed, speed);


    }

}
