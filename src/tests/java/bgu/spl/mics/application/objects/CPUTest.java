package bgu.spl.mics.application.objects;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class CPUTest {
    private static CPU cpu;
    DataBatch dataBatch;
    
     @Before
    public void setUp() throws Exception{
        cpu = new CPU(32,new Cluster(), 0);
        dataBatch = new DataBatch(0, new Data(Data.Type.Text.toString(),1000));

    }

    public void addDataTest(){
        //setup
        int preSize = cpu.getDataSize();

        cpu.addData(dataBatch);

        assertEquals("Error if the data list size has not" +
                " increase by one",preSize + 1, cpu.getDataSize());

    }
    public void testUpdateTick(){
        // setup
        int preTick = cpu.getTick();
        cpu.updateTick(preTick + 1);
        // assert
        assertEquals(preTick + 1,cpu.getTick());
    }

    public void processTest(){
        //setup
        cpu.addData(dataBatch);
        int startTick = cpu.getTick();
        //before
        assertFalse("Error if processed", dataBatch.IsProcessed());

        DataBatch d = cpu.process();
        cpu.updateTick(startTick + 1);
        assertFalse("error if processed before time", d.IsProcessed());
        cpu.updateTick(startTick + 2);
        //after
        assertTrue("Error if not processed", d.IsProcessed());
        assertEquals(startTick+cpu.TicksToProcess(d), cpu.getTick());
    }

    public void ticksTest(){
        assertEquals("Expected 2", 2 ,cpu.TicksToProcess(dataBatch));
    }
    

    

}
