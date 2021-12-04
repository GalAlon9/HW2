package bgu.spl.mics.application.objects;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class GPUTest {
    private static GPU gpu;
    private Model model;
    private Cluster cluster;
    
    @Before
    public void setUp() throws Exception{
        model = new Model();
        cluster = new Cluster();
        gpu = new GPU("RTX3090",model,cluster);
    }
    
   

}
