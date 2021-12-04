package bgu.spl.mics;
import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap<MicroService, Queue<Message>> MSqueueMap;
	private HashMap<Class<? extends Event> , Queue<MicroService>> eventMap;
	private HashMap<Class<? extends Broadcast> , HashSet<MicroService>> broadcastMap;
	private HashMap<Class<? extends Event> , Future> futureMap;

	private MessageBusImpl(){
		MSqueueMap = new HashMap<>();
		eventMap = new HashMap<>();
		broadcastMap = new HashMap<>();
		futureMap = new HashMap<>();
	}


	/**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
	 * @Pre the MicroService is registered
	 * @Post the MicroService is subscribed to the Event -> m is aded 
	 * to this Event q in EventMap
     */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}
	/**
	 * 
	 * @param <T> The type of the result expected by the completed event.
	 * @param type The type to check if subscribed to
	 * @param m The subscribing micro-service.
	 * @return returns if the service is subscired to this Event
	 */
	public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m){
		return false;
	}
	/**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * @param type 	The type to subscribe to.
     * @param m    	The subscribing micro-service.
	 * @Pre the MicroService is registered
	 * @Post the MicroService is subscribed to the broadcast -> m is aded 
	 * to this Broadcast Microservices container in BroadcastMap
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}
	public <T> boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m){
		return false;
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}
	public <T> boolean isComplete(Event<T> e){
		return false; 
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}
	public boolean isBroadcastSent(Broadcast b){
		return false;
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public <T> boolean isEventSent(Event<T> e){
		return false;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	public boolean isRegistered(MicroService m){
		return false;
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
