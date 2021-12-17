package bgu.spl.mics;

import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private final ConcurrentHashMap<MicroService, Queue<Message>> MSqueueMap;
    private final ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventMap;
    private final ConcurrentHashMap<Class<? extends Broadcast>, HashSet<MicroService>> broadcastMap;
    private final ConcurrentHashMap<Event, Future> futureMap;
    private final Object MSqueueLocker;
    private final Object eventLocker;
    private final Object broadcastLocker;
    private final Object futureLocker;

    private MessageBusImpl() {
        MSqueueMap = new ConcurrentHashMap<>();
        eventMap = new ConcurrentHashMap<>();
        broadcastMap = new ConcurrentHashMap<>();
        futureMap = new ConcurrentHashMap<>();
        MSqueueLocker = new Object();
        eventLocker = new Object();
        broadcastLocker = new Object();
        futureLocker = new Object();
    }

    private static class SingletonHolder {
        private static final MessageBusImpl instance = new MessageBusImpl();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }


    /**
     * @Pre the MicroService is registered -> isRegistered(m) == true
     * @Post the MicroService is subscribed to the Event -> isSubscribedToEvent(type,m)==true
     * to this Event q in EventMap
     */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (eventLocker) {
            if (eventMap.containsKey(type)) {
                eventMap.get(type).add(m);
            } else {
                Queue<MicroService> MSQ = new LinkedList<MicroService>();
                MSQ.add(m);
                eventMap.put(type, MSQ);
            }
        }

    }

    /**
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to check if subscribed to
     * @param m    The subscribing micro-service.
     * @return returns if the service is subscribed to this Event
     * @pre: none
     * @post: @pre(isSubscribedToEvent(Event,m)) == @post(isSubscribedToEvent(Event,m))
     */
    public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (eventLocker) {
            if (eventMap.containsKey(type)) {
                return eventMap.get(type).contains(m);
            }
        }
        return false;
    }

    /**
     * @Pre the MicroService is registered -> isRegistered(m) == true
     * @Post the MicroService is subscribed to the broadcast -> isSubscribedToBroadcast(type,m)==true
     * to this Broadcast MicroServices container in BroadcastMap
     */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (broadcastLocker) {
            if (broadcastMap.containsKey(type)) {
                broadcastMap.get(type).add(m);
            } else {
                HashSet<MicroService> broadcastList = new HashSet<MicroService>();
                broadcastList.add(m);
                broadcastMap.put(type, broadcastList);
            }
        }
    }

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     *
     * @param type The type to subscribe to.
     * @param m    The subscribing micro-service.
     * @pre: none
     * @post: @pre(isSubscribedToBroadcast(Event,m)) == @post(isSubscribedToBroadcast(Event,m))
     */
    public <T> boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (broadcastLocker) {
            if (broadcastMap.containsKey(type)) {
                return broadcastMap.get(type).contains(m);
            }
            return false;
        }
    }

    /**
     * @pre: isComplete(e) == false
     * @inv: isEventSent(e) == true
     * @post: isComplete(e) == true
     */
    @Override
    public <T> void complete(Event<T> e, T result) {
        synchronized (futureLocker) {
            if (futureMap.containsKey(e)) {
                futureMap.get(e).resolve(result);
            }
            futureMap.remove(e);
        }

    }


    /**
     * @return if the event future has been resolved
     * @pre: None
     * @post: @pre(isComplete(e)) == @post(isComplete(e))
     */
    public <T> boolean isComplete(Event<T> e) {
        synchronized (futureLocker) {
            if (futureMap.containsKey(e)) {
                return futureMap.get(e).isDone();
            }
            return false;
        }
    }

    /**
     * @pre: None
     * @inv
     * @post: all microServices subscribed to broadcast class received the message
     */
    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (broadcastLocker) {
            if (broadcastMap.containsKey(b.getClass()) && !broadcastMap.get(b.getClass()).isEmpty())
                synchronized (MSqueueLocker) {
                    for (MicroService microService : broadcastMap.get(b.getClass())) {
                        MSqueueMap.get(microService).add(b);

                    }
                    MSqueueLocker.notifyAll();
                }

        }
    }

    /**
     * @pre: None
     * @post: one of the subscribed microServices has received the Event message
     */
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = null;
        synchronized (eventLocker) {
            if (eventMap.containsKey(e.getClass()) && !eventMap.get(e.getClass()).isEmpty()) {
                synchronized (MSqueueLocker) {
                    MicroService m = eventMap.get(e.getClass()).poll();
                    MSqueueMap.get(m).add(e);
                    eventMap.get(e.getClass()).add(m);

                    synchronized (futureLocker) {
                        futureMap.put(e, new Future<T>());
                        future = futureMap.get(e);
                    }
                    MSqueueLocker.notifyAll();
                }
            }
        }
        return future;

    }

    /**
     * @pre: isRegistered(m) == false
     * @post: isRegistered(m) == true
     */
    @Override
    public void register(MicroService m) {
        synchronized (MSqueueLocker) {
            if (!isRegistered(m)) {
                MSqueueMap.put(m, new LinkedList<>());
            }
        }
    }

    /**
     * @pre: None
     * @post: @pre(isRegistered(m)) == @post(isRegistered(m))
     */
    public boolean isRegistered(MicroService m) {
        synchronized (MSqueueLocker) {
            return MSqueueMap.containsKey(m);
        }
    }

    /**
     * @pre: isRegistered(m) == true
     * @post: isRegistered(m) == false && all reference of m removed from the messageBus
     */
    @Override
    public void unregister(MicroService m) {
        synchronized (MSqueueLocker) {
            if (!isRegistered(m)) {
                return;
            }
            MSqueueMap.remove(m);
        }
        synchronized (eventLocker) {
            for (Queue<MicroService> c : eventMap.values()) {
                c.remove(m);
            }
        }
        synchronized (broadcastLocker) {
            for (HashSet<MicroService> hashSet : broadcastMap.values()) {
                hashSet.remove(m);
            }
        }
    }

    /**
     * @pre: None
     * @inv: isRegistered(m) == true
     */
    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        synchronized (MSqueueLocker) {
            while (MSqueueMap.get(m).isEmpty()) {
                MSqueueLocker.wait();
            }
            return MSqueueMap.get(m).poll();
        }
    }

    public void clear(){
        synchronized (futureLocker) {
            futureMap.clear();
        }
        synchronized (broadcastLocker) {
            broadcastMap.clear();
        }
        synchronized (eventLocker) {
            eventMap.clear();
        }
        synchronized (MSqueueLocker) {
            MSqueueMap.clear();
        }
    }


}
