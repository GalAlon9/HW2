package bgu.spl.mics.application.objects;


import java.util.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int id;
    private int cores;
    private Queue<DataBatch> dataQueue;
    private Cluster cluster;
    private int currTick;
    private int timeToWait;
    private boolean isProcessing;
    private Object cpuLock;

    public CPU(int id, int cores) {
        this.id = id;
        this.cores = cores;
        this.dataQueue = new LinkedList<>();
        this.cluster = Cluster.getInstance();
        this.currTick = 0;
        this.timeToWait = 0;
        this.isProcessing = false;
        this.cpuLock = new Object();
    }

    /**
     * @param dBatch - dataBatch to process
     * @inv the cpu can receive data to process
     */
    public void addData(DataBatch dBatch) {
        dataQueue.add(dBatch);
        timeToWait += ticksToProcess(dBatch);
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
            while (getTick() < startTick + ticksToProcess(data)) {
                try {
                    synchronized (cpuLock) {
                        cpuLock.wait();
                    }
                    timeToWait--;
                    cluster.increaseCpuTime();
                } catch (InterruptedException e) {
                }
            }
            System.out.println("out");
            data.process();
            cluster.increaseProcessedData();
            isProcessing = false;
            cluster.receiveDataFromCPUSendToGPU(data);
        }
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    /**
     * @pre: None
     * @post: currTick = tick
     */
    public void updateTick(int tick) {
        synchronized (cpuLock) {
            currTick = tick;
            cpuLock.notifyAll();
        }
    }

    public int getTick() {
        return currTick;
    }

    /**
     * @param dataBatch - based on dataBatch type and number of cores calculates
     *                  how many ticks required to process the data
     * @return how many ticks required to process the data
     */
    public int ticksToProcess(DataBatch dataBatch) {
        if (dataBatch.getData().getType() == Data.Type.Images) {
            return (32 / cores) * 4;
        }
        if (dataBatch.getData().getType() == Data.Type.Text) {
            return (32 / cores) * 2;
        } else {
            return 32 / cores;
        }
    }

    public int getTimeToWait() {
        return timeToWait;
    }

    public int getDataSize() {
        return dataQueue.size();
    }
}
