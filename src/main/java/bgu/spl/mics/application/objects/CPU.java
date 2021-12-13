package bgu.spl.mics.application.objects;


import java.util.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Queue<DataBatch> dataQueue;
    private Cluster cluster;
    private int currTick;

    public CPU(int cores) {
        this.cores = cores;
        dataQueue = new LinkedList<DataBatch>();
        this.cluster = Cluster.getInstance();
        currTick = 0;
    }


    /**
     * @param dBatch - dataBatch to process
     * @inv the cpu can receive data to process
     */
    public void addData(DataBatch dBatch) {
        dataQueue.add(dBatch);
        timeToWait += TicksToProcess(dBatch);
    }

    /**
     * @return the processed dataBatch
     * @Pre the DataBatch isn't processed
     * @Post the DataBatch is processed
     */
    public void process() {
        if (!dataQueue.isEmpty()) {
            isProcessing = true;
            DataBatch data = this.dataQueue.poll();
            int startTick = getTick();
            try {
                while (getTick() < startTick + TicksToProcess(data)) {
                    this.wait();
                    timeToWait--;
                    cluster.increaseCpuTime();
                }
                data.process();
                cluster.increaseProcessedData();
            } catch (InterruptedException e) {
            }
            isProcessing = false;
            cluster.receiveDataFromCPUSendToGPU(data);
        }
    }
    public boolean isProcessing(){
        return isProcessing;
    }

    /**
     * @pre: None
     * @post: currTick = tick
     */
    public void updateTick(int tick) {
        currTick = tick;
    }

    public int getTick() {
        return currTick;
    }

    /**
     * @param dataBatch - based on dataBatch type and number of cores calculates
     *                  how many ticks required to process the data
     * @return how many ticks required to process the data
     */
    public int TicksToProcess(DataBatch dataBatch){
        // if(dataBatch.getData().getType() == Data.Type.Images) return(32/cores)*4;
        // if(dataBatch.getData().getType() == Data.Type.Text) return(32/cores)*2;
        // else return 32/cores;
        return 0;

    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public int getDataSize() {
        return dataQueue.size();
    }
}
