package bgu.spl.mics.application.objects;


import java.util.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private final Queue<DataBatch> dataQueue = new LinkedList<>();
    private final Cluster cluster = Cluster.getInstance();
    private int currTick = 0;
    private int timeToWait = 0;
    private boolean isProcessing = false;
    private int endProcessingTick = 0;
    private DataBatch currentDB = null;

    public CPU(int cores) {
        this.cores = cores;

    }

    /**
     * @param db - dataBatch to process
     * @inv the cpu can receive data to process
     */
    public void addData(DataBatch db) {
        dataQueue.add(db);
        timeToWait += ticksToProcess(db);
    }

    /**
     * @return the processed dataBatch
     * @pre the DataBatch isn't processed
     * @post the DataBatch is processed
     */
    public void process() {
        if (!dataQueue.isEmpty()) {
            isProcessing = true;
            currentDB = this.dataQueue.poll();
            endProcessingTick = getTick() + ticksToProcess(currentDB);
        }
    }

    private void doneProcess() {
        currentDB.process();
        cluster.increaseProcessedData();
        isProcessing = false;
        cluster.receiveDataFromCPUSendToGPU(currentDB);
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    /**
     * @pre: None
     * @post: currTick = tick
     */
    public void updateTick(int tick) {
        currTick = tick;
        if(isProcessing()){
            timeToWait--;
            cluster.increaseCpuTime();
            if(getTick() == endProcessingTick){
                doneProcess();
            }
        }
        if (!isProcessing()) {
            process();
        }
    }

    public int getTick() {
        return currTick;
    }

    /**
     * @param db - based on dataBatch type and number of cores calculates
     *                  how many ticks required to process the data
     * @return how many ticks required to process the data
     */
    public int ticksToProcess(DataBatch db) {
        int ticks = 0;
        if (db.getData().getType().equals(Data.Type.Images)) {
            ticks = (32 / cores) * 4;
        }
        else if (db.getData().getType().equals(Data.Type.Text)) {
            ticks =  (32 / cores) * 2;
        } else {
            ticks = 32 / cores;
        }
        return  ticks;
    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public int getDataSize() {
        return dataQueue.size();
    }
}
