package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;



public class TestModelEvent implements Event {
    Model model;

    public TestModelEvent(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public boolean isGoodResult (Model model){
        if(model.getResult() == Model.Result.Good) return true; else return false;
    }
}
