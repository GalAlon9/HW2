package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    private static List<Thread> threadList = new LinkedList<>();

    public static void main(String[] args) {
        initializeAll("example_input.json");
        try {
            // Starts all the threads and initializes all the services
            for (Thread thread : threadList) {
                thread.start();
            }
            // Starts all the threads and initializes all the services
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JsonObject jsonOutPut = new JsonObject();
        JsonArray students = new JsonArray();

        System.out.println("cpu time used " + Cluster.getInstance().

                getCpuTime());
        System.out.println("gpu time used " + Cluster.getInstance().

                getGpuTime());
        System.out.println("data batch processed " + Cluster.getInstance().

                getProcessedData());
        ConcurrentLinkedQueue<String> modelsTrained = Cluster.getInstance().getModelsTrained();
        System.out.println("Models trained :");
        for (
                String model : modelsTrained) {
            System.out.println(model);
        }
        System.out.println("finish");

    }

    public static void initializeAll(String path) {
        try {
            List<MicroService> msList = new LinkedList<>();

            Object object = new JsonParser().parse(new FileReader(path));
            JsonObject jsonObject = (JsonObject) object;

            JsonArray students = (JsonArray) jsonObject.get("Students");
            studentsInitialize(students, msList);

            JsonArray GPUs = (JsonArray) jsonObject.get("GPUS");
            GPUSInitialize(GPUs, msList);

            JsonArray CPUs = (JsonArray) jsonObject.get("CPUS");
            CPUSInitialize(CPUs, msList);

            JsonArray conferences = (JsonArray) jsonObject.get("Conferences");
            ConferencesInitialize(conferences, msList);


            int tickTime = jsonObject.get("TickTime").getAsInt();
            int duration = jsonObject.get("Duration").getAsInt();
            TimeService timeService = new TimeService(tickTime, duration);
            msList.add(timeService);

            // Starts all the threads and initializes all the services
            for (MicroService microService : msList) {
                Thread thread = new Thread(microService);
                thread.setName(microService.getName());
                threadList.add(thread);
            }

        } catch (FileNotFoundException exception) {
            System.out.println("file didn't found");
        }
    }

    public static void studentsInitialize(JsonArray students, List<MicroService> msList) {
        for (JsonElement student : students) {
            JsonObject currStudent = student.getAsJsonObject();
            String name = currStudent.get("name").getAsString();
            String department = currStudent.get("department").getAsString();
            String status = currStudent.get("status").getAsString();
            JsonArray models = (JsonArray) currStudent.get("models");
            Student student1;
            if (status.equals("MSc")) {
                student1 = new Student(name, department, Student.Degree.MSc);
            } else {
                student1 = new Student(name, department, Student.Degree.PhD);
            }
            for (JsonElement model : models) {
                JsonObject currModel = model.getAsJsonObject();
                String modelName = currModel.get("name").getAsString();
                String DataType = currModel.get("type").getAsString();
                int DataSize = currModel.get("size").getAsInt();
                Data data = new Data(DataType, DataSize);
                Model model1 = new Model(modelName, data, student1);
                student1.addModel(model1);
            }
            StudentService studentService = new StudentService(student1);
            msList.add(studentService);
        }
    }

    public static void GPUSInitialize(JsonArray GPUs, List<MicroService> msList) {
        Cluster cluster = Cluster.getInstance();
        for (JsonElement gpu : GPUs) {
            String GPUType = gpu.getAsString();
            GPU.Type type = GPUType.equals("RTX3090") ? GPU.Type.RTX3090 : GPUType.equals("RTX2080") ? GPU.Type.RTX2080 : GPU.Type.GTX1080;
            GPU gpu1 = new GPU(type);
            GPUService gpuService = new GPUService(gpu1);
            cluster.addGPU(gpu1);
            msList.add(gpuService);
        }
    }

    public static void CPUSInitialize(JsonArray CPUs, List<MicroService> msList) {
        Cluster cluster = Cluster.getInstance();
        for (JsonElement cpu : CPUs) {
            int cores = cpu.getAsInt();
            CPU cpu1 = new CPU(cores);
            CPUService cpuService = new CPUService(cpu1);
            cluster.addCPU(cpu1);
            msList.add(cpuService);
        }
    }

    public static void ConferencesInitialize(JsonArray conferences, List<MicroService> msList) {
        int start = 0;
        for (JsonElement conferenceElement : conferences) {
            JsonObject conferenceObject = conferenceElement.getAsJsonObject();
            String name = conferenceObject.get("name").getAsString();
            int finish = conferenceObject.get("date").getAsInt();
            ConferenceInformation conferenceInformation = new ConferenceInformation(name, start, finish);
            ConferenceService conferenceService = new ConferenceService(conferenceInformation);
            msList.add(conferenceService);
            start = finish;
        }
    }

}
