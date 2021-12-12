package bgu.spl.mics.application;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import jdk.internal.util.xml.impl.Input;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        initializeAll(args[0]);
    }

    public static void initializeAll(String path){
        try {
            Object object = new JsonParser().parse(new FileReader(path));
            JsonObject jsonObject = (JsonObject) object;
            JsonArray students = (JsonArray)jsonObject.get("Students");
            JsonArray GPUs = (JsonArray)jsonObject.get("GPUS");
            JsonArray CPUs = (JsonArray)jsonObject.get("CPUS");
            JsonArray conferences = (JsonArray)jsonObject.get("Conferences");
            int tickTime = jsonObject.get("TickTime").getAsInt();
            int duration = jsonObject.get("Duration").getAsInt();
            TimeService timeService = new TimeService(tickTime,duration);


            MessageBusImpl messageBus = MessageBusImpl.getInstance();

            messageBus.register(timeService);

            studentsInitialize(students,messageBus);
            GPUSInitialize(GPUs,messageBus);
            CPUSInitialize(CPUs,messageBus);
            ConferencesInitialize(conferences,messageBus);




        }catch (FileNotFoundException exception){
            System.out.println("file didn't found");
        }
    }

    public static void studentsInitialize(JsonArray students,MessageBusImpl messageBus){
        for(JsonElement student: students){
            JsonObject currStudent = student.getAsJsonObject();
            String name = currStudent.get("name").getAsString();
            String department = currStudent.get("department").getAsString();
            String status = currStudent.get("status").getAsString();
            JsonArray models = (JsonArray)currStudent.get("models");
            Student student1 = new Student(name,department,status);
            for(JsonElement model :models){
                JsonObject currModel = model.getAsJsonObject();
                String modelName = currModel.get("name").getAsString();
                String DataType = currModel.get("type").getAsString();
                int DataSize = currModel.get("size").getAsInt();
                Data data = new Data(DataType,DataSize);
                Model model1 = new Model(modelName,data,student1);
                student1.addModel(model1);
            }
            messageBus.register(new StudentService(student1.getName(),student1));
        }
    }

    public static void GPUSInitialize(JsonArray GPUs,MessageBusImpl messageBus){
        for(JsonElement gpu: GPUs){
            String GPUType = gpu.getAsString();
            GPU gpu1 = new GPU(GPUType);
            GPUService gpuService = new GPUService(GPUType,gpu1);
            messageBus.register(gpuService);
        }
    }

    public static void CPUSInitialize(JsonArray CPUs, MessageBusImpl messageBus){
        for(JsonElement cpu : CPUs){
            int cores = cpu.getAsInt();
            CPU cpu1 = new CPU(cores);
            CPUService cpuService = new CPUService("CPU service",cpu1);
            messageBus.register(cpuService);
        }
    }

    public static void ConferencesInitialize(JsonArray conferences, MessageBusImpl messageBus){
        for(JsonElement conferenceElement : conferences){
            JsonObject conferenceObject = conferenceElement.getAsJsonObject();
            String name = conferenceObject.get("name").getAsString();
            int date = conferenceObject.get("date").getAsInt();
            ConferenceInformation conferenceInformation = new ConferenceInformation(name,date);
            ConferenceService conferenceService = new ConferenceService("conference Service", conferenceInformation);
            messageBus.register(conferenceService);
        }
    }

}
