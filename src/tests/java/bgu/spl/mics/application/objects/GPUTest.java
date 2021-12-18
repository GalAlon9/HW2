package bgu.spl.mics.application.objects;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GPUTest {
    private static GPU gpu;
    private Model model;
    private Cluster cluster;
    private Data data;

    @Before
    public void setUp() throws Exception {
        cluster = Cluster.getInstance();
        gpu = new GPU(GPU.Type.RTX3090);
        data = new Data("Images", 10000);
        model = new Model("model", data, null);
    }

    @Test
    public void setNextModel() {
        //before
        assertNull("some model has been assigned", gpu.getModel());
        //set
        gpu.addTrainModel(model);
        gpu.setNextModel();
        //after
        assertNotNull("no model has been assigned to the gpu", gpu.getModel());
    }

    @Test
    public void extractBatchesFromDisk() {
        //setup
        gpu.addTrainModel(model);
        gpu.setNextModel();
        gpu.prepareBatches();
        //set
        DataBatch db = gpu.extractBatchesFromDisk();
        //after
        assertNotNull("no dataBatch has been extracted", db);
    }

    @Test
    public void receiveProcessedData() {
        //setup
        DataBatch db = new DataBatch(0, data);
        //before
        int preCapacity = gpu.getVRAMSize();
        //set
        gpu.receiveProcessedData(db);
        //after
        assertEquals(preCapacity + 1, gpu.getVRAMSize());
    }

    @Test
    public void prepareBatches() {
        // setup
        gpu.addTrainModel(model);
        gpu.setNextModel();
        // before
        assertTrue(gpu.getDiskSize() == 0);
        // set
        gpu.prepareBatches();
        // after
        assertTrue(gpu.getDiskSize() == model.getData().Size()/1000);

    }
    @Test
    public void startTrainingDB(){
        // setup
        gpu.addTrainModel(model);
        gpu.setNextModel();
        DataBatch db = new DataBatch(0, data);
        //before
        gpu.receiveProcessedData(db);
        int preVramSize = gpu.getVRAMSize();
        // set
        gpu.startTrainingDB();
        // assert
        assertEquals(preVramSize,gpu.getVRAMSize() + 1);
    }


    @Test
    public void updateTick() {
        // setup
        int preTick = gpu.getTick();
        gpu.updateTick(preTick + 1);
        // assert
        assertEquals(preTick + 1, gpu.getTick());
    }

    // doesn't compile because cluster needs to be init with cpus
//    @Test
//    public void sendToCluster(){
//        // setup
//        gpu.addTrainModel(model);
//        gpu.setNextModel();
//        DataBatch db = new DataBatch(0, data);
//        // before
//        gpu.prepareBatches();
//        int preDiskSize = gpu.getDiskSize();
//        int preVramCapaciy = gpu.getCapacity();
//        System.out.println(preDiskSize);
//        //set
//        gpu.sendToCluster();
//        // assert
//        assertEquals(Math.max(preDiskSize -gpu.getCapacity(),0),gpu.getDiskSize());
//        assertEquals(gpu.getCapacity(),preVramCapaciy - (gpu.getDiskSize() - preDiskSize));
//    }

}
