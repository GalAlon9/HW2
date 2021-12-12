package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int tick;

    public TickBroadcast(int currTick){
        tick = currTick;
    }

    public  int get(){
        return  tick;
    }
}
