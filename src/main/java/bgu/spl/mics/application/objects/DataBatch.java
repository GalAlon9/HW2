package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private boolean isProcessed;
    private Data data;
    private int start_index;

    public DataBatch(int start_index,Data data){
        isProcessed = false;
        this.start_index = start_index;
        this.data = data;
    }


    /**
     * @Pre dataBatch isnt processed -> isProcessed == false
     * @Post dataBatch is processed -> isProcessed == true
     */
    public void process(){
        isProcessed = true;
    }

    public boolean IsProcessed(){
        return isProcessed;
    }
    
}
