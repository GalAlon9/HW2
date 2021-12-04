package bgu.spl.mics.application.objects;


import java.util.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Queue<DataBatch> data ;
    private Cluster cluster;
    private int currTick;

    public CPU(int cores){
        cores = cores;
        data = new LinkedList<DataBatch>();
    }
    public void addData(DataBatch dBatch){
        data.add(dBatch);
    }
    /**
     * @Pre the DataBatch isnt processed
     * @Post the DataBatch is processed
     * @return the processed dataBatch 
     */
    public DataBatch process(){
        DataBatch toProcess = this.data.poll();
        toProcess.process();
        return toProcess;
    }
    public void updateTick(){
        //currTick = ********
    }
    /**
     * 
     * @param dataBatch - based on dataBatch type and number of cores calculates how many ticks required to process the data
     * @return how many ticks required to process the data
     */
    private int TicksToProcess(DataBatch dataBatch){
        // if(dataBatch.getData().getType() == Data.Type.Images) return(32/cores)*4;
        // if(dataBatch.getData().getType() == Data.Type.Text) return(32/cores)*2;
        // else return 32/cores;
        return 0;

    }

}
