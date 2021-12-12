package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConferenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    int currTick;
    ConferenceInformation conference;
    public ConferenceService(ConferenceInformation conference) {
        super("Conference");
        this.currTick = 0;
        this.conference = conference;
    }



    @Override
    protected void initialize() {
        // subscribe to terminate broadcast
        subscribeBroadcast(TerminateBroadcast.class, t -> terminate());

        subscribeBroadcast(TickBroadcast.class , tickBroadcast ->{
            increaseTick(tickBroadcast.get());
        });

    }
    private void increaseTick(int tick){
        currTick = tick;
        if(currTick == conference.getStart()){
            subscribeEvent(PublishResultsEvent.class, results -> {
                conference.addPublication(results.getModel());
            });
        }
        if(currTick == conference.getFinish()){
            sendBroadcast(new PublishConferenceBroadcast(conference));
            terminate();
        }
    }
}
