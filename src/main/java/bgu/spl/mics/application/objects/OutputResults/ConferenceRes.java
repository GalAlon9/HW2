package bgu.spl.mics.application.objects.OutputResults;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class ConferenceRes {
    private String name;
    private int date;
    private List<ModelRes> publications;

    public ConferenceRes(String name,int date,List<ModelRes> publications){
        this.name = name;
        this.date = date;
        this.publications = publications;
    }

    public JsonObject conResToJson(){
        JsonObject conObj = new JsonObject();
        conObj.addProperty("name", name);
        conObj.addProperty("date" ,date);
        JsonArray jsonArray = new JsonArray();
        for(ModelRes res : publications){
            jsonArray.add(res.modelResToJson());
        }
        conObj.add("publications",jsonArray);
        return conObj;
    }
}
