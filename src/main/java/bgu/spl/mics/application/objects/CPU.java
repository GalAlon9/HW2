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
        this.cores = cores;
        data = new LinkedList<DataBatch>();
        this.cluster = Cluster.getInstance();
        currTick = 0;
    }

    public CPU(int cores) {
        cores = cores;
    }

    /**
     * @inv the cpu can receive data to process
     * @param dBatch - dataBatch to process
     */
    public void addData(DataBatch dBatch){
        data.add(dBatch);
    }
    /**
     * @Pre the DataBatch isn't processed
     * @Post the DataBatch is processed
     * @return the processed dataBatch 
     */
    public DataBatch process(){
        DataBatch data = this.data.poll();
        int startTick = getTick();
        while(getTick() < startTick + TicksToProcess(data)){
        }
        // DataBatch toProcess = this.data.poll();
        // toProcess.process();
        // return toProcess;
        return null;
    }
    /**
    * @pre: None
    * @post: currTick = tick
     */
    public void updateTick(int tick){
        currTick = tick;
    }

    public int getTick(){
        return currTick;
    }
    /**
     * 
     * @param dataBatch - based on dataBatch type and number of cores calculates
     *                    how many ticks required to process the data
     * @return how many ticks required to process the data
     */
    public int TicksToProcess(DataBatch dataBatch){
        // if(dataBatch.getData().getType() == Data.Type.Images) return(32/cores)*4;
        // if(dataBatch.getData().getType() == Data.Type.Text) return(32/cores)*2;
        // else return 32/cores;
        return 0;

    }
    public int getDataSize(){
        return data.size();
    }

}
