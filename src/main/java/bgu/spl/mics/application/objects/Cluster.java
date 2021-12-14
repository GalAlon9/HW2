package bgu.spl.mics.application.objects;


import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
    private List<GPU> gpus;
//    private ConcurrentHashMap<DataBatch, GPU> dataMap;
    private PriorityQueue<CPU> cpuMinHeap;
    private AtomicInteger cpuTime;
    private ConcurrentLinkedQueue<String> modelsTrained;
    private AtomicInteger processedData;
    private AtomicInteger gpuTime;

    /**
     * Retrieves the single instance of this class.
     */
    private Cluster() {
        gpus = new LinkedList<>();
//        dataMap = new ConcurrentHashMap<>();
        cpuMinHeap = new PriorityQueue<>(Comparator.comparingInt(CPU::getTimeToWait));
        cpuTime = new AtomicInteger();
        gpuTime = new AtomicInteger();
        processedData = new AtomicInteger();
        modelsTrained = new ConcurrentLinkedQueue<String>();

    }

    public static Cluster getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Cluster instance = new Cluster();
    }

    public void addCPU(CPU cpu) {
        this.cpuMinHeap.add(cpu);
    }

    public void addGPU(GPU gpu) {
        this.gpus.add(gpu);
    }

    public void receiveDataFromGPUSendToCPU(DataBatch db) {
//        dataMap.put(db, gpu);
        assert cpuMinHeap.peek() != null;
        CPU receiver = cpuMinHeap.peek();
        receiver.addData(db);
    }

    public void receiveDataFromCPUSendToGPU(DataBatch db) {
        if(db.IsProcessed()) {
            db.getGpu().receiveProcessedData(db);
//            dataMap.remove(db);
        }
    }


    public int getProcessedData() {
        return processedData.get();
    }

    public int getCpuTime() {
        return cpuTime.get();
    }

    public int getGpuTime() {
        return gpuTime.get();
    }

    public Object[] getModelsTrained() {
        return modelsTrained.toArray();
    }

    public void increaseGpuTime() {
        gpuTime.incrementAndGet();
    }

    public void increaseCpuTime() {
        cpuTime.incrementAndGet();
    }

    public void addModel(String model) {
        modelsTrained.add(model);
    }

    public void increaseProcessedData() {
        processedData.incrementAndGet();
    }


}


