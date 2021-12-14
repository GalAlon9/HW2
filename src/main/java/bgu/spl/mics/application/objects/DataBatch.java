package bgu.spl.mics.application.objects;

import java.lang.reflect.AnnotatedType;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private boolean isProcessed;
    private Data data;
    private int start_index;
    private GPU gpu;

    public DataBatch(int start_index, Data data) {
        isProcessed = false;
        this.start_index = start_index;
        this.data = data;
        this.gpu = null;

    }


    /**
     * @Pre dataBatch isnt processed -> isProcessed == false
     * @Post dataBatch is processed -> isProcessed == true
     */
    public synchronized void process() {
        isProcessed = true;
    }

    public synchronized boolean IsProcessed() {
        return isProcessed;
    }

    public synchronized Data getData() {
        return data;
    }

    public synchronized void setGpu(GPU gpu) {
        this.gpu = gpu;
    }

    public synchronized GPU getGpu() {
        return gpu;
    }
}
