package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> models;

    public Student(String name, String department, Degree status) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.models = new ArrayList<>();
    }

    public void addModel(Model model1) {
        models.add(model1);
    }
    public List<Model> getModels(){
        return models;
    }

    public String getName() {
        return name;
    }

}
