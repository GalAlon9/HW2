package bgu.spl.mics.application.objects.OutputResults;

import bgu.spl.mics.application.objects.Cluster;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;

public class OutputJson {
    private LinkedList<StudentRes> studentResults;
    private LinkedList<ConferenceRes> conferenceResults;
    private int GPUTime;
    private int CPUTime;
    private int batches;

    private OutputJson() {
        studentResults = new LinkedList<>();
        conferenceResults = new LinkedList<>();
        GPUTime = 0;
        CPUTime = 0;
        batches = 0;
    }

    public static OutputJson getInstance() {
        return OutputJson.SingletonHolder.instance;
    }
    private static class SingletonHolder {
        private static final OutputJson instance = new OutputJson();
    }

    public void addStudentRes(StudentRes studentRes){studentResults.add(studentRes);}
    public void addConferenceRes(ConferenceRes conferenceRes){conferenceResults.add(conferenceRes);}
    public void setGPUTime(int time){GPUTime = time;}
    public void setCPUTime(int time){CPUTime = time;}
    public void setBatches(int amount){batches = amount;}

    public JsonObject writeOutput(){
        JsonObject output = new JsonObject();
        JsonArray students = new JsonArray();
        for(StudentRes res : studentResults){
            students.add(res.studentResultsToJson());
        }
        JsonArray conferences = new JsonArray();
        for(ConferenceRes res :conferenceResults){
            conferences.add(res.conResToJson());
        }
        output.add("students",students);
        output.add("conferences",conferences);
        output.addProperty("cpuTimeUsed",CPUTime);
        output.addProperty("gpuTimeUsed",GPUTime);
        output.addProperty("batchesProcessed",batches);
        return output;
    }


}



