package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.GPUService;

import java.util.*;
import java.util.Random;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {

    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model currModel;
    private Cluster cluster;
    private LinkedList<DataBatch> Disk;
    private LinkedList<DataBatch> VRAM;
    private int VRAM_Capacity;
    private int currTick;
    private boolean isTraining;
    private Queue<Model> testModelQueue;
    private Queue<Model> trainModelQueue;
    private GPUService gpuService;


    /**
     * @param typ the type of the GPU can be “RTX3090”, “RTX2080”, ”GTX1080”.
     */
    public GPU(Type typ) {
        cluster = Cluster.getInstance();
        currModel = null;
        type = typ;
        VRAM_Capacity = type == Type.GTX1080 ? 8 : type == Type.RTX2080 ? 16 : 32;
        Disk = new LinkedList<DataBatch>();
        VRAM = new LinkedList<DataBatch>();
        currTick = 0;
        isTraining = false;
        gpuService = null;
        testModelQueue = new LinkedList<>();
        trainModelQueue = new LinkedList<>();

    }

    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }

    public void sendToCluster() {
        while (!Disk.isEmpty() && getCapacity() > 0) {
            DataBatch unProcessedData = extractBatchesFromDisk();
            cluster.receiveDataFromGPUSendToCPU(unProcessedData);
            VRAM_Capacity--;
        }
    }

    public void addTestModel(Model model) {
        testModelQueue.add(model);
    }

    public void addTrainModel(Model model) {
        trainModelQueue.add(model);
    }

    /**
     * @pre: model == null
     * @post: this.model = model
     */
    // the gpu service assign the model to the gpu
    public void setModel() {
        this.currModel = null;
        if (!trainModelQueue.isEmpty()) {
            this.currModel = trainModelQueue.poll();
            currModel.setStatus(Model.Status.Training);
        }
    }

    public Model getModel() {
        return this.currModel;
    }

    public int getCapacity() {
        return VRAM_Capacity;
    }

    /**
     * @return unprocessed dataBatch
     * @inv !isDoneTraining()
     * @pre: disk is not empty
     * @post: @pre(disk.size()) == @post(disk.size() - 1)
     */
    // gets the un-processed data batch from the disk
    public DataBatch extractBatchesFromDisk() {
        return Disk.poll();
    }

    /**
     * @inv: getCapacity() >= 1
     * @post: @pre(getCapacity()) == @post(getCapacity()) + 1
     */
    //gets the processed data from cluster and puts it in VRAM
    public void receiveProcessedData(DataBatch processedDataBatch) {
        VRAM.add(processedDataBatch);
    }

    // prepare batches from model.data and insert the batches into the disk
    private void prepareBatches() {
        for (int i = 0; i < currModel.getData().Size(); i += 1000) {
            DataBatch db = new DataBatch(i, currModel.getData());
            db.setGpu(this);
            Disk.add(db);
        }
    }

    /**
     * @pre: None
     * @inv: !vram.isEmpty() && getModel() != null
     * @post: @pre(data.proccesed) = @post(data.proccesed - 1)
     */
    public synchronized void train() {
        if (!VRAM.isEmpty()) {
            DataBatch processedDataBatch = VRAM.poll();
            isTraining = true;
            DataBatch data = this.VRAM.poll();
            int startTick = getTick();
            try {
                while (getTick() < startTick + TicksToProcess()) {
                    wait();
                    cluster.increaseGpuTime();
                }
            } catch (InterruptedException e) {
            }
            isTraining = false;
            currModel.getData().increaseProcessed();
        }
        VRAM_Capacity++;
        if (isDoneTraining()) {
            doneTrain();
        }
        sendToCluster();
    }

    /**
     * @return if training is done -> data.proccesed == data.size
     */
    public boolean isDoneTraining() {
        if (currModel != null) {
            return currModel.getData().getProcessed() == currModel.getData().Size();
        }
        return true;
    }

    private void doneTrain() {
        if(currModel != null){
            currModel.setStatus(Model.Status.Trained);
            gpuService.completeEvent(getModel());
            cluster.addModel(currModel.getName()); // add model to trained models statistics
        }
    }

    public synchronized void updateTick(int tick) {
        currTick = tick;
        notifyAll();
    }

    public boolean isTraining() {
        return isTraining;
    }

    private int TicksToProcess() {
        if (type == Type.RTX3090) return 1;
        if (type == Type.RTX2080) return 2;
        else return 4;
    }

    private int getTick() {
        return currTick;
    }

    // test models first than train the next model
    public void work() {
        if (isDoneTraining()) {//only when initializing or finish to train model
            testModels();
            setModel();
            if (currModel != null) {
                prepareBatches();
                sendToCluster();
            }
        }
        train();

    }

    // test the next models in the queue
    private void testModels() {
        while (!testModelQueue.isEmpty()) {
            Model model = testModelQueue.poll();
            Model.Result result = getResult(model);
            model.setResult(result);
            gpuService.completeEvent(model); // resolve the future of the testModelEvent
        }
    }

    // returns the result of the model
    private Model.Result getResult(Model model) {
        Random rnd = new Random();
        double prob = rnd.nextDouble();
        if (model.getStudent().getStatus().equals(Student.Degree.MSc)) {
            return prob <= 0.6 ? Model.Result.Good : Model.Result.Bad;
        } else return prob <= 0.8 ? Model.Result.Good : Model.Result.Bad;
    }

}
