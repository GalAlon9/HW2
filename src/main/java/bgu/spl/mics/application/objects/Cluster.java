package bgu.spl.mics.application.objects;



import java.util.LinkedList;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.compare;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Cluster {
    private final List<GPU> gpus;
    private final PriorityBlockingQueue cpuMinHeap;
    private final List<CPU> cpuList;
    private final AtomicInteger cpuTime;
    private final ConcurrentLinkedQueue<String> modelsTrained;
    private final AtomicInteger processedData;
    private final AtomicInteger gpuTime;
    private Object lock1 = new Object();
    private Object lock2 = new Object();


    /**
     * Retrieves the single instance of this class.
     */
    private Cluster() {
        gpus = new LinkedList<>();
        cpuMinHeap = new PriorityBlockingQueue();
        cpuList = new LinkedList<>();
        cpuTime = new AtomicInteger();
        gpuTime = new AtomicInteger();
        processedData = new AtomicInteger();
        modelsTrained = new ConcurrentLinkedQueue<>();

    }
    public int cpuComparator(CPU a, CPU b){
        return b.getTimeToWait() - a.getTimeToWait();
    }

    public static Cluster getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Cluster instance = new Cluster();
    }

    public void addCPU(CPU cpu) {
//        this.cpuMinHeap.offer(cpu);
        cpuList.add(cpu);
    }

    public void addGPU(GPU gpu) {
        this.gpus.add(gpu);
    }

    public void receiveDataFromGPUSendToCPU(DataBatch db) {
        synchronized (lock1) {
//            assert cpuMinHeap.peek() != null;
//            CPU receiver = cpuMinHeap.peek();
            CPU minCPU = cpuList.get(0);
            for(CPU cpu : cpuList){
                if(cpu.getTimeToWait() < minCPU.getTimeToWait()){
                    minCPU = cpu;
                }
            }
            minCPU.addData(db);
            if(minCPU.getTick()% 5000  < 10){
                int x = 2;
            }
        }
    }

    public void receiveDataFromCPUSendToGPU(DataBatch db) {
        if(db.IsProcessed()) {
            synchronized (lock2) {
                db.getGpu().receiveProcessedData(db);
            }
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

    public ConcurrentLinkedQueue<String> getModelsTrained() {
        return modelsTrained;
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


