package bgu.spl.mics;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;

public class MessegeBusTest{

    private static MessageBus mb;
    private static MicroService broadcastListener;
    private static MicroService eventHandler;
    private static MicroService MessageSender;
    private static ExampleBroadcast broadcast;
    private static ExampleEvent event;

    @Before
     public void setUp() throws Exception{
        mb = new MessageBus();
        broadcastListener = new ExampleBroadcastListenerService();
        eventHandler = new ExampleEventHandlerService();
        messageSender = new ExampleMessageSenderService();
        broadcast  = new ExampleBroadcast("0");    
        event = new ExampleEvent("Gal");
    }

    @After
    public void tearDown() throws Exception {
        mb.Clear();
    }


    @Test
    public void testSubscribeEvent() {
        //pre
        assertFalse("Error if ms is subscribed", mb.isSubscribedToEvent(event, eventHandler));
        
        mb.subscribeEvent(event, eventHandler);
        assertTrue("Error if ms is not subscribed",  mb.isSubscribedToEvent(event, eventHandler));
    }

    @Test
    public void testSubscribeBroadcast() {
        //pre
        assertFalse("Error if ms is subscribed", mb.isSubscribedToBroadcast(broadcast, broadcastListener));
        //post
        mb.subscribeBroadcast(broadcast, broadcastListener);
        assertTrue("Error if ms is not subscribed", mb.isSubscribedToBroadcast(broadcast, broadcastListener));
    }

    @Test
    public void testComplete() {
        // pre
        assertFalse("Error if completed", mb.isComplete(event));
        mb.complete(event, "someResult");
        assertTrue("Errpr of not completed", mb.isComplete(event));
    }

    @Test
    public void testSendBroadcast() {
        //pre
        assertFalse("Error if broadcast is sent", mb.isBroadcastSent(broadcast));
        
        mb.sendBroadcast(broadcast);

        //post
        assertTrue("Error if broadcast is not sent", mb.isBroadcastSent(broadcast));
    }

    @Test
    public void testSendEvent() {
        // pre
         assertFalse("Error if event is sent", mb.isEventSent(event));
         mb.sendEvent(event);
         //post
        assertTrue("Error if broadcast is  sent", mb.isBroadcastSent(broadcast));

    }

    @Test
    public void testRegister() {
        assertFalse("Error if is registered", mb.isRegister(messageSender));
        mb.register(messageSender);
        assertTrue("Error if not registered", mb.isRegister(messageSender));
    }

    @Test
    public void testUnregister() {
        assertTrue("Error if not registered", mb.isRegister(messageSender));
        mb.unregister(messageSender);
        assertFalse("Error if is registered", mb.isRegister(messageSender));
    }

    @Test
    public void testAwaitMessage() {
        //setup
        mb.register(broadcastListener);
        mb.subscribeBroadcast(broadcast,broadcastListener);
        mb.sendBroadcast(broadcast);
        try{
            Message message = mb.awaitMessage(broadcastListener);
            assertEquals("Error if not equal",broadcast, message);
        }
        catch(InterruptedException exception){
            exception.printStackTrace();
        }

    }
}
