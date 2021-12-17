package bgu.spl.mics;
import static org.junit.Assert.*;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;

public class MessageBusTest {

    private static MessageBusImpl mb;
    private static MicroService broadcastListener1;
    private static MicroService broadcastListener2;
    private static MicroService eventHandler1;
    private static MicroService eventHandler2;
    private static MicroService messageSender;
    private static ExampleBroadcast broadcast;
    private static ExampleEvent event;

    @Before
     public void setUp() throws Exception{
        mb = MessageBusImpl.getInstance();
        broadcastListener1 = new ExampleBroadcastListenerService("example",new String[]{"1"});
        broadcastListener2 = new ExampleBroadcastListenerService("example",new String[]{"1"});
        eventHandler1 = new ExampleEventHandlerService("example",new String[]{"1"});
        eventHandler2 = new ExampleEventHandlerService("example",new String[]{"1"});
        messageSender = new ExampleMessageSenderService("example",new String[]{"event"});
        broadcast  = new ExampleBroadcast("0");    
        event = new ExampleEvent("Gal");
    }

    @After
    public void tearDown() throws Exception {
        mb.clear();
    }


    @Test
    public void testSubscribeEvent() {
        //pre
        assertFalse("Error if ms is subscribed", mb.isSubscribedToEvent(event.getClass(), eventHandler1));
        
        mb.subscribeEvent(event.getClass(), eventHandler1);
        assertTrue("Error if ms is not subscribed",  mb.isSubscribedToEvent(event.getClass(), eventHandler1));
    }

    @Test
    public void testSubscribeBroadcast() {
        //pre
        assertFalse("Error if ms is subscribed", mb.isSubscribedToBroadcast(broadcast.getClass(), broadcastListener1));
        //post
        mb.subscribeBroadcast(broadcast.getClass(), broadcastListener1);
        assertTrue("Error if ms is not subscribed", mb.isSubscribedToBroadcast(broadcast.getClass(), broadcastListener1));
    }

    @Test
    public void testComplete() {
        // setup
        mb.register(eventHandler1);
        mb.subscribeEvent(event.getClass(),eventHandler1);
        mb.sendEvent(event);
        // pre
        assertFalse("Error if completed", mb.isComplete(event));
        mb.complete(event, "someResult");
        assertTrue("Error of not completed", mb.isComplete(event));
    }

    @Test
    public void testSendBroadcast() {
        //setup
        Message  m1 , m2;
        mb.register(broadcastListener1);
        mb.register(broadcastListener2);
        mb.subscribeBroadcast(broadcast.getClass(), broadcastListener1);
        mb.subscribeBroadcast(broadcast.getClass(), broadcastListener2);
        mb.sendBroadcast(broadcast);
        //test
        try{
            m1= mb.awaitMessage(broadcastListener1);
            assertEquals("the micro service didn't receive the broadcast",broadcast, m1);



        }catch(InterruptedException exception){
            exception.printStackTrace();
        }
        try{
            m2= mb.awaitMessage(broadcastListener2);
            assertEquals("the micro service didn't receive the broadcast",broadcast, m2);


        }catch(InterruptedException exception){
            exception.printStackTrace();
        }


        
    }

    @Test
    public void testSendEvent() {
        //setup
        Future<String> future;
        mb.register(eventHandler1);
        // pre
        assertNull(mb.sendEvent(event));
        // set
        mb.subscribeEvent(ExampleEvent.class, eventHandler1);
        future = mb.sendEvent(event);
        // post
        assertNotNull(future);

    }

    @Test
    public void testRegister() {
        assertFalse("Error if registered", mb.isRegistered(messageSender));
        mb.register(messageSender);
        assertTrue("Error if not registered", mb.isRegistered(messageSender));
    }

    @Test
    public void testUnregister() {
        // set
        mb.register(messageSender);
        // test
        assertTrue("Error if not registered", mb.isRegistered(messageSender));
        mb.unregister(messageSender);
        assertFalse("Error if is registered", mb.isRegistered(messageSender));
    }

    @Test
    public void testAwaitMessage() {
        //setup
        mb.register(broadcastListener1);
        mb.subscribeBroadcast(broadcast.getClass(),broadcastListener1);
        mb.sendBroadcast(broadcast);
        try{
            Message message = mb.awaitMessage(broadcastListener1);
            assertEquals("Error if not equal",broadcast, message);
        }
        catch(InterruptedException exception){
            exception.printStackTrace();
        }

    }
}
