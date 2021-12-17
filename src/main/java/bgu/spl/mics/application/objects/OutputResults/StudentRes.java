package bgu.spl.mics.application.objects.OutputResults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class StudentRes {
    private String name;
    private String department;
    private String status;
    private int publications;
    private int papersRead;
    private List<ModelRes> trainedModels;

    public StudentRes(String name,String department,String status,int publications,int papersRead,List<ModelRes> trainedModels){
        this.name = name;
        this.department = department;
        this.status = status;
        this.papersRead = papersRead;
        this.publications = publications;
        this.trainedModels = trainedModels;
    }

    public JsonObject studentResultsToJson(){
        JsonObject studentsObj = new JsonObject();
        studentsObj.addProperty("name",name);
        studentsObj.addProperty("department",department);
        studentsObj.addProperty("status",status);
        studentsObj.addProperty("publications",publications);
        studentsObj.addProperty("papersRead",papersRead);
        JsonArray jsonArray = new JsonArray();
        for(ModelRes modelRes : trainedModels){
            jsonArray.add(modelRes.modelResToJson());
        }
        studentsObj.add("trainedModels",jsonArray);
        return studentsObj;
    }
}
