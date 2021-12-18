package bgu.spl.mics.application.objects;

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

    private final Type type;
    private Model currModel = null;
    private final Cluster cluster = Cluster.getInstance();
    private final LinkedList<DataBatch> Disk = new LinkedList<>();
    private final LinkedList<DataBatch> VRAM = new LinkedList<>();
    private int VRAM_Capacity;
    private int currTick = 0;
    private boolean isTraining = false;
    private final Queue<Model> testModelQueue = new LinkedList<>();
    private final Queue<Model> trainModelQueue = new LinkedList<>();
    private GPUService gpuService = null;
    private int endTrainingDBTick = 0;


    /**
     * @param typ the type of the GPU can be “RTX3090”, “RTX2080”, ”GTX1080”.
     */
    public GPU(Type typ) {
        type = typ;
        resetVRAMCapacity();
    }
    private void resetVRAMCapacity(){
        this.VRAM_Capacity = (type == Type.GTX1080) ? 8 : ((type == Type.RTX2080) ? 16 : 32);
    }
    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }


    /**
     * @pre !disk.isEmpty && vramCapacity > 0
     * @post @post(disk.size) = max(@pre(disk.size) - vramCapacity , 0)
     *       post(vram capacity) = @pre(Vram capacity) - (@post(disk.size) - @pre(disk.size))
     *
     */
    private void sendToCluster() {
        while (!Disk.isEmpty() && getCapacity() > 0) {
            DataBatch unProcessedData = extractBatchesFromDisk();
            if (unProcessedData == null) {
                int x = 2;
            }
            cluster.receiveDataFromGPUSendToCPU(unProcessedData);
            VRAM_Capacity--;
        }
    }

    /**
     * @param model
     * @post @post(testModelQueue.size) = @pre(testModelQueue.size) + 1
     */
    public void addTestModel(Model model) {
        testModelQueue.add(model);
    }
    /**
     * @param model
     * @post @post(trainModelQueue.size) = @pre(trainModelQueue.size) + 1
     */
    public void addTrainModel(Model model) {
        trainModelQueue.add(model);
    }

    /**
     * @pre: model == null
     * @post: this.model = model
     */
    // the gpu service assign the model to the gpu
    public void setNextModel() {
        this.currModel = null;
        if (!trainModelQueue.isEmpty()) {
            this.currModel = trainModelQueue.poll();
            System.out.println("start training model " + currModel.getName());
        }
    }

    public Model getModel() {
        return this.currModel;
    }

    public int getCapacity() {
        return VRAM_Capacity;
    }

    /**
     * gets the un-processed data batch from the disk
     * @return unprocessed dataBatch
     * @inv !isDoneTraining()
     * @pre: disk is not empty
     * @post: @pre(disk.size()) == @post(disk.size() - 1)
     */
    public DataBatch extractBatchesFromDisk() {
        return Disk.poll();
    }

    /**
     * gets the processed data from cluster and puts it in VRAM
     * @post: @post(vram.size) = @pre(vram.size) + 1
     */
    public void receiveProcessedData(DataBatch processedDataBatch) {
        VRAM.add(processedDataBatch);
    }

    /**
     * prepare batches from model.data and insert the batches into the disk
     * @pre: currModel != null
     * @inv: disk.isEmpty
     * @post: disk.size = model.data.size /1000
     */
    public void prepareBatches() {
        if (currModel != null) {
            for (int i = 0; i < currModel.getData().Size(); i += 1000) {
                DataBatch db = new DataBatch(i, currModel.getData());
                if (db == null) {
                    int x = 2;
                }
                db.setGpu(this);
                Disk.add(db);
            }
        }
    }

    /**
     * train the next dataBatch in vram
     * @pre: None
     * @inv: !vram.isEmpty() && getModel() != null
     * @post: @post(vram.size) = @pre(vram.size) + 1
     */
    public void startTrainingDB() {
        if (!VRAM.isEmpty() && currModel != null) {
            isTraining = true;
            VRAM.poll();
            endTrainingDBTick = getTick() + ticksToTrain();
            currModel.getData().increaseProcessed();
        }
    }

    /**
     * @return if training is done -> data.proccesed == data.size
     */
    private boolean isDoneTrainingModel() {
        if (currModel != null) {
            return currModel.getData().getProcessed() >= currModel.getData().Size();
        }
        return true;
    }

    private void doneTrainingModel() {
        if (currModel != null) {
            gpuService.completeEvent(getModel());
            cluster.addModel(currModel.getName()); // add model to trained models statistics
            isTraining = false;
            resetVRAMCapacity();
        }
    }

    /**
     * update the current tick and make the gpu act
     * @param tick
     * @pre: none
     * @post: @post(tick) = @pre(tick) + 1
     */
    public void updateTick(int tick) {
        currTick = tick;
        if (isTraining()) {
            cluster.increaseGpuTime();
            if (getTick() == endTrainingDBTick) {
                VRAM_Capacity++;
                sendToCluster();
            }
            else{
                return;
            }
        }
        if (isDoneTrainingModel()) {  // finished training model
            doneTrainingModel();
            testModels(); // test available models
            setNextModel(); // set next model to train
            prepareBatches();
            sendToCluster();
        }
        startTrainingDB();

    }

    public boolean isTraining() {
        return isTraining;
    }


    // returns ticks required to train the model depends on the gpu type
    public int ticksToTrain() {
        int ticks = 0;
        if (type.equals(Type.RTX3090)) {
            ticks = 1;
        }
        else if (type.equals(Type.RTX2080)){
            ticks = 2;
        }
        else ticks = 4;
        return ticks;
    }

    private int getTick() {
        return currTick;
    }


    // test the next models in the queue
    private void testModels() {
        while (!testModelQueue.isEmpty()) {
            Model model = testModelQueue.poll();
            Model.Result result = getResult(model);
            model.setResult(result);
            // resolve the future of the testModelEvent
            gpuService.completeEvent(model);
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
