package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    public GPU(String gpuType) {

    }

    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model currModel;
    private Cluster cluster;
    private LinkedList<DataBatch> Disk;
    private LinkedList<DataBatch> VRAM;
    private int VRAM_Capacity;
    private int currTick;
    private boolean isProcessing;
    /**
     *
     * @param typ the type of the GPU can be “RTX3090”, “RTX2080”, ”GTX1080”.
     */
    public GPU (Type typ){
        cluster = Cluster.getInstance();
        currModel = null;
        type = typ;
        VRAM_Capacity = type==Type.GTX1080?8: type==Type.RTX2080?16 :32;
        Disk = new LinkedList<DataBatch>();
        VRAM = new LinkedList<DataBatch>();
        currTick = 0;
        isProcessing = false;
     }

     public void sendToCluster(){
         if(!Disk.isEmpty() && getCapacity() > 0){
             VRAM_Capacity--;
             DataBatch unProcessedData = extractBatchesFromDisk();
             cluster.receiveDataFromGPUSendToCPU(unProcessedData);
         }
     }
    /**
     * @pre: model == null
     * @post: this.model = model
     */
     // the gpu service assign the model to the gpu
     public void setModel(Model model){
         this.currModel = model;
         // preparebatches()
     }
     public Model getModel(){
         return this.currModel;
     }

     public int getCapacity(){
         return VRAM_Capacity;
     }
     /**
      * @inv !isDoneTraining()
      * @pre: disk is not empty
      * @post: @pre(disk.size()) == @post(disk.size() - 1)
      * @return unprocessed dataBatch 
      */
    // gets the un-processed data batch from the disk
    
    public DataBatch extractBatchesFromDisk(){
        return Disk.poll();
    }

    /**
    * @inv: getCapacity() >= 1 
    * @post: @pre(getCapacity()) == @post(getCapacity()) + 1
     */
    //gets the processed data from cluster and puts it in VRAM
    public void receiveProcessedData(DataBatch processedDataBatch){
        VRAM.add(processedDataBatch);
    }
    // prepare batches from model.data and insert the batches into the disk
    private void prepareBatches(){
        // divide the data / 1000
    }
    /**
    * @pre: None
    * @inv: !vram.isEmpty() && getModel() != null
    * @post: @pre(data.proccesed) = @post(data.proccesed - 1) 
     */
    public void train(){
        // processedDataBatch = vram.pop()
        // data.proccesed += processedDataBatch from vram
        if (!VRAM.isEmpty()) {
            isProcessing = true;
            DataBatch data = this.VRAM.poll();
            int startTick = getTick();
            try {
                while (getTick() < startTick + TicksToProcess()) {
                    this.wait();
                    cluster.increaseGpuTime();
                }
            } catch (InterruptedException e) {
            }
            isProcessing = false;
        }
//        if(Disk.isEmpty()){
//            doneTrain();
//        }
    }
    /**
    * @return if training is done -> data.proccesed == data.size
     */
    public boolean isDoneTraining(){
        return false;
    }

    private void doneTrain(){
        cluster.addModel(currModel.getName());
    }

    public void updateTick(int tick) {
        currTick = tick;
        notifyAll();
    }
    public boolean isProcessing(){
        return isProcessing;
    }
    private int TicksToProcess() {
        if (type == Type.RTX3090) return 1;
        if (type == Type.RTX2080) return 2;
        else return 4;
    }
    private int getTick(){
        return currTick;
    }

}
