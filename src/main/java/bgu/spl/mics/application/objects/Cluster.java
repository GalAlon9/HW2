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
    private final List<CPU> cpuList;
    private final AtomicInteger cpuTime;
    private final ConcurrentLinkedQueue<String> modelsTrained;
    private final AtomicInteger processedData;
    private final AtomicInteger gpuTime;
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    public int dataSentFromGPUS = 0;
    public int dataSentFromCPUS = 0;



    /**
     * Retrieves the single instance of this class.
     */
    private Cluster() {
        gpus = new LinkedList<>();
        cpuList = new LinkedList<>();
        cpuTime = new AtomicInteger();
        gpuTime = new AtomicInteger();
        processedData = new AtomicInteger();
        modelsTrained = new ConcurrentLinkedQueue<>();

    }
//    public int cpuComparator(CPU a, CPU b){
//        if(b.getTimeToWait() == a.getTimeToWait()){
//            return b.getCores() - a.getCores();
//        }
//        return b.getTimeToWait() - a.getTimeToWait();
//    }

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
            dataSentFromGPUS++;
            CPU minCPU = minTimeCpu();
            minCPU.addData(db);
        }
    }

    private CPU minTimeCpu(){
        CPU minCPU = cpuList.get(0);
        for(CPU cpu : cpuList){
            if(cpu.getTimeToWait() < minCPU.getTimeToWait()){
                minCPU = cpu;
            }
            else if(cpu.getTimeToWait() == minCPU.getTimeToWait()){
                minCPU = cpu.getCores() > minCPU.getCores() ? cpu : minCPU;
            }
        }
        return minCPU;
    }

    public void receiveDataFromCPUSendToGPU(DataBatch db) {
            synchronized (lock2) {
                dataSentFromCPUS++;
                db.getGpu().receiveProcessedData(db);
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


