package bgu.spl.mics.application.objects.OutputResults;

import bgu.spl.mics.application.objects.Data;
import com.google.gson.JsonObject;

public class ModelRes {
    private String name;
    private Data data;
    private String status;
    private String result;

    public ModelRes(String name,Data data,String status,String result){
        this.data = data;
        this.name = name;
        this.result = result;
        this.status = status;
    }

    public JsonObject modelResToJson(){
        JsonObject modelObj = new JsonObject();
        modelObj.addProperty("name",name);
        JsonObject dataObj = new JsonObject();
        dataObj.addProperty("type", data.toString());
        dataObj.addProperty("size", data.Size());
        modelObj.add("data", dataObj);
        modelObj.addProperty("status",status);
        modelObj.addProperty("result",result);
        return modelObj;
    }
//    public String toString(){
//        String output="";
//
//    }
}
