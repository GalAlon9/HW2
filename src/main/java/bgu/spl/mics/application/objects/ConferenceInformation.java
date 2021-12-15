package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private final String name;
    private final HashMap<Student, List<Model>> publicationsByStudent;
    private int size = 0;
    private final int start;
    private final int finish;
    private List<Model> allModels;

    public ConferenceInformation(String name, int start, int finish) {
        this.name = name;
        publicationsByStudent = new HashMap();
        this.start = start;
        this.finish = finish;
        this.allModels = new LinkedList<>();

    }

    public void addPublication(Model model) {
        Student student = model.getStudent();
        List<Model> models;
        if (publicationsByStudent.containsKey(student)) {
            models = publicationsByStudent.get(student);
            models.add(model);
            publicationsByStudent.put(student, models);
        } else {
            models= new LinkedList<>();
            publicationsByStudent.put(student, models);
        }
        allModels.add(model);
        size++;
    }

    public int getNumOfPublishedByStudent(Student student) {
        if (!publicationsByStudent.containsKey(student)) {
            return 0;
        }
        return publicationsByStudent.get(student).size();
    }

    public int getSize() {
        return size;
    }

    public int getStart() {
        return start;
    }

    public int getFinish() {
        return finish;
    }

    public String getName() {
        return name;
    }
    public int getDate(){
        return finish;
    }

    public List<Model> getModelList() {
        return allModels;
    }
}
