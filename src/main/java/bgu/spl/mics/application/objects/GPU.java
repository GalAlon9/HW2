package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
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
     * @param mdl the model the GPU is currently working on
     * @param clstr The compute cluster
     */
    public GPU (Type typ, Model mdl, Cluster clstr){
        cluster = clstr;
        model = mdl;
        type = typ;
        VRAM_Capacity = type==Type.GTX1080?8: type==Type.RTX2080?16 :32;
        Disk = new LinkedList<DataBatch>();
        VRAM = new LinkedList<DataBatch>(); 

     }
    // /**
    //  * 
    //  * @return returns the model this GPU works on
    //  */
    // public Model getModel(){
    //     return model;
    // }
    // /**
    //  * 
    //  * @return returns the GPU type
    //  */
    // public Type getType(){
    //     return type;
    // }
    // /**
    //  * 
    //  * @return returns the computre cluster this GPU is part of
    //  */
    // public Cluster getCluster(){
    //     return cluster;
    // }

    public DataBatch getBatches(){
        return null;
    }
    //gets the processed data from cluster and puts it in VRAM
    public Data getProcessedData(){
        return null;
    }
    
    
    public void buildDataFromBatches(DataBatch dataBatch){

    }

    public void prepareBatches(){

    }


}
