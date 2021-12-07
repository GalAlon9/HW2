package bgu.spl.mics;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import javax.sound.sampled.SourceDataLine;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class FutureTest{
    private Future<String> future;
    private String res;

    @Before
    public void setUp() throws Exception{
        future = new Future<String>();
        res = "test";
    }

    @Test
    public void  testGet(){
        //arrange
        future.resolve(res);
        //set
        String toCheck=future.get();
        //assert
        assertTrue(future.isDone());
        assertEquals("the expected result from get is not equal to resolve's result",res,toCheck);
    }
   

    @Test
    public void TestResolve(){
        //set
        future.resolve(res);
        //assert
        assertEquals(res,future.get());
        assertTrue(future.isDone());
    }

    @Test
    public void TestIsDone(){
        //pre
        assertFalse(future.isDone());
        //set
        future.resolve(res);
        //assert
        assertTrue(future.isDone());

    }
     @Test
    public void testGetByTime(){
        try{
            assertNull(future.get(100,TimeUnit.MILLISECONDS));
            //this.Thread.sleep(5);
            future.resolve(res);
            assertEquals(res,future.get(100,TimeUnit.MILLISECONDS));

        }
        catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        
    }

    
}    
