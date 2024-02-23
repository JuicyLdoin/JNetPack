package net.jnetpack.worker;

import net.jnetpack.event.interfaces.IEvent;

public interface IJNetWorker<T> {

    /**
     * @param t - object
     */
    void addToQueue(T t);

    /**
     * @return - queue is full
     */
    boolean isOverloaded();

    /**
     * Call event
     *
     * @param event - target event
     */
    void callEvent(IEvent event);

    /**
     * Shutdown worker
     */
    void close();
}