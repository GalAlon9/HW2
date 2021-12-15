package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.application.objects.Student;

public class PublishConferenceBroadcast implements Broadcast {
    private ConferenceInformation conference;

    public PublishConferenceBroadcast(ConferenceInformation conference) {
        this.conference = conference;
    }

    public int getPublished(Student student) {
        return conference.getNumOfPublishedByStudent(student);
    }

    public int getRead(Student student) {
        return conference.getSize() - getPublished(student);
    }
}
