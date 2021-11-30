package bgu.spl.mics;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class FutureTest{
    private static Future<T> future;

    @Before
    public void setUp() throws Exception{
        future = new Future<>();
    }
    
    @Test
    public void  testGet(){
        
    }

    @Test
    public void TestResolve(){
        String res = "this result";
        future.resolve(res);
        assertEquals(res,future.get());
        assertTrue(future.isDone());
    }
}    
