package bgu.spl.mics.application.objects;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private int date;
    private HashMap<Student,Integer> publicationsByStudent;
    private int size;
    private int start;
    private int finish;
    private LinkedList<Model> modelList;

    public ConferenceInformation(String name, int start, int finish) {
        this.name = name;
        publicationsByStudent = new HashMap();
        size = 0;
        this.start = start;
        this.finish = finish;
        this.modelList = new LinkedList<>();
    }

    public void addPublication(Model model){
        modelList.add(model);
        Student student = model.getStudent();
        if(publicationsByStudent.containsKey(student)){
            int num = publicationsByStudent.get(student);
            publicationsByStudent.put(student,num+1);
        }
        else{
            publicationsByStudent.put(student,1);
        }
        size++;
    }
    public int getPublishedByStudent(Student student){
        if(!publicationsByStudent.containsKey(student)){
            return 0;
        }
        return publicationsByStudent.get(student);
    }
    public LinkedList<Model> getModelList(){return modelList;}

    public int getSize(){
        return size;
    }
    public int getStart(){
        return start;
    }
    public int getFinish(){
        return finish;
    }
    public String getName(){return name;}
    public int getDate(){return date;}
}
