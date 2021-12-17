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
        VRAM_Capacity = (type == Type.GTX1080) ? 8 : ((type == Type.RTX2080) ? 16 : 32);
    }

    public void setGpuService(GPUService gpuService) {
        this.gpuService = gpuService;
    }

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
     * @pre: None
     * @inv: !vram.isEmpty() && getModel() != null
     * @post: @pre(data.proccesed) = @post(data.proccesed - 1)
     */
    public void train() {
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
            currModel.setStatus(Model.Status.Trained);
            gpuService.completeEvent(getModel());
            cluster.addModel(currModel.getName()); // add model to trained models statistics
        }
    }

    public void updateTick(int tick) {
        currTick = tick;
        if (isTraining()) {
            cluster.increaseGpuTime();
            if (getTick() == endTrainingDBTick) {
                VRAM_Capacity++;
                sendToCluster();
            }
        }
        if (isDoneTrainingModel()) {  // finished training model
            doneTrainingModel();
            testModels(); // test next models if available
            setNextModel(); // train the the next model
            prepareBatches();
            sendToCluster();
        }
        train();
    }

    public boolean isTraining() {
        return isTraining;
    }

    private int ticksToTrain() {
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
            model.setStatus(Model.Status.Tested);
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
