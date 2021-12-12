package bgu.spl.mics.application.objects;

import com.google.gson.JsonElement;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    String name;
    Data data;
    Student student;
    Status status;
    Result result;
    public Model(String modelName, Data data, Student student) {
        this.name = modelName;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.result = Result.None;
    }
    enum Status{
        PreTrained,Training,Trained,Tested
    }
    enum Result{
        None,Good,Bad
    }
    public Data getData(){
        return data;
    }
    public Student getStudent(){
        return getStudent();
    }
    public Status getStatus(){
        return status;
    }
    public Result getResult(){
        return result;
    }
    public void setStatus(Status status){
        this.status = status;
    }
    public void setResult(Result result){
        this.result = result;
    }
}
