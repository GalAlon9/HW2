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
    private Model model;
    private Cluster cluster;
    private LinkedList<DataBatch> Disk;
    private LinkedList<DataBatch> VRAM;
    private int VRAM_Capacity;

    /**
     * 
     * @param typ the type of the GPU can be “RTX3090”, “RTX2080”, ”GTX1080”.
     * @param clstr The compute cluster
     */
    public GPU (Type typ, Cluster clstr){
        cluster = clstr;
        model = null;
        type = typ;
        VRAM_Capacity = type==Type.GTX1080?8: type==Type.RTX2080?16 :32;
        Disk = new LinkedList<DataBatch>();
        VRAM = new LinkedList<DataBatch>(); 

     }
     /**
     * @pre: model == null
     * @post: this.model = model
     */
     // the gpu service assign the model to the gpu
     public void setModel(Model model){
         this.model = model;
         // preparebatches()
     }
     public Model getModel(){
         return this.model;
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
        return null;
    }

    /**
    * @inv: getCapacity() >= 1 
    * @post: @pre(getCapacity()) == @post(getCapacity()) + 1
     */
    //gets the processed data from cluster and puts it in VRAM
    public void receiveProcessedData(DataBatch processedDataBatch){

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
    }
    /**
    * @return if training is done -> data.proccesed == data.size
     */
    public boolean isDoneTraining(){
        return false;
    }
 


}
