package bgu.spl.mics.application.objects;
import static org.junit.Assert.*;

import org.junit.Before;

public class CPUTest {
    private static CPU cpu;
    DataBatch dataBatch;
    
     @Before
    public void setUp() throws Exception{
        cpu = new CPU(1, 32);
        dataBatch = new DataBatch(0, new Data("Text",1000));

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
        cpu.process();
        cpu.updateTick(startTick + 1);
        assertFalse("error if processed before time", dataBatch.IsProcessed());
        cpu.updateTick(startTick + 2);
        //after
        assertTrue("Error if not processed", dataBatch.IsProcessed());
        assertEquals(startTick+cpu.ticksToProcess(dataBatch), cpu.getTick());
    }

    public void ticksTest(){
        assertEquals("Expected 2", 2 ,cpu.ticksToProcess(dataBatch));
    }
    

    

}
