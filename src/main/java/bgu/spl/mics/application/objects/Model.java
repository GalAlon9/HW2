package bgu.spl.mics.application.objects;

import com.google.gson.JsonElement;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String modelName, Data data, Student student) {
        this.name = modelName;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.result = Result.None;
    }

    public enum Status {
        PreTrained, Training, Trained, Tested, Testing
    }
    public String getName(){
        return this.name;
    }
    public enum Result {
        None, Good, Bad
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String statusToString(){
        return status.equals(Status.PreTrained)? "PreTrained" : status.equals(Status.Tested)? "Tested" : status.equals(Status.Training)? "Training":"Tested";
    }
    public String resultToString(){
        return result.equals(Result.Bad)? "Bad" :result.equals(Result.Good)? "Good":"None";
    }
}
