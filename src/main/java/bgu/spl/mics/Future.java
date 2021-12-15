package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * <p>
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

    private T result;
    private boolean isDone;


    /**
     * This should be the the only public constructor in this class.
     */
    public Future() {
        isDone = false;
        result = null;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     *
     * @return return the result of type T if it is available, if not wait until it is available.
     * @pre: none
     * @post: the future has been resolved -> result != null && isDone == true
     */
    public synchronized T get() {
        while (!isDone) {
            try {
                wait();
            } catch (InterruptedException exception) {}

        }
        return result;
    }

    /**
     * Resolves the result of this Future object.
     *
     * @param result
     * @pre isDone = false
     * @post isDone = true
     */
    public synchronized void resolve(T result) {
            isDone = true;
            this.result = result;
            notifyAll();
    }

    /**
     * @return true if this object has been resolved, false otherwise
     * @pre: none
     * @post: @pre(isDone) == @post(isDone)
     */
    public synchronized boolean isDone() {
        return isDone;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit    the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not,
     * wait for {@code timeout} TimeUnits {@code unit}. If time has
     * elapsed, return null.
     * @pre: None
     * @post: if returned null -> isDone = false
     * else -> isdone = true
     */
    public synchronized T get(long timeout, TimeUnit unit) {
        if (isDone()) {
            try {
                unit.timedWait(this, timeout);
            } catch (InterruptedException exception) {}
        }
        return result;
    }
    }
