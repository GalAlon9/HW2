package bgu.spl.mics.application.objects;

import java.util.HashMap;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private final String name;
    private int date;
    private final HashMap<Student,Integer> publicationsByStudent;
    private int size;
    private final int start;
    private final int finish;

    public ConferenceInformation(String name, int start, int finish) {
        this.name = name;
        publicationsByStudent = new HashMap();
        size = 0;
        this.start = start;
        this.finish = finish;
    }

    public void addPublication(Model model){
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

    public int getSize(){
        return size;
    }
    public int getStart(){
        return start;
    }
    public int getFinish(){
        return finish;
    }
}
