package bgu.spl.mics.application.objects;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class GPUTest {
    private static GPU gpu;
    private Model model;
    private Cluster cluster;
    private Data data;
    
    @Before
    public void setUp() throws Exception{
        cluster = Cluster.getInstance();
        gpu = new GPU(GPU.Type.RTX3090);
        data = new Data("Images", 100);
        model = new Model();
    }
    
    public void testExtractData(){
        //setup
        DataBatch db;
        gpu.setModel(model);
        //set
        db = gpu.extractBatchesFromDisk();
        //after
        assertNotNull("no dataBatch has been extracted",db);
    }
    public void setModelTest(){
        //before
        assertNull("some model has been assigned",gpu.getModel());
        //set
        gpu.setModel(model);
        //after
        assertNotNull("no model has been assigned to the gpu",gpu.getModel());
    }
    public void trainTest(){
        //setup
        DataBatch db = new DataBatch(0, data);
        db.process();
        gpu.receiveProcessedData(db);
        //before
        int preProcessed = data.getProcessed();
        //set
        gpu.train();
        //after
        assertEquals("the gpu didn't process any more data",preProcessed+1, data.getProcessed());

    }

    public void testReceiveProcessedData(){
        //setup
        DataBatch db = new DataBatch(0, data);
        //before
        int preCapacity = gpu.getCapacity();
        //set
        gpu.receiveProcessedData(db);
        //after
        assertEquals(preCapacity-1, gpu.getCapacity());
    }
   

}
