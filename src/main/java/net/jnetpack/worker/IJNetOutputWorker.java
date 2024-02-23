package net.jnetpack.worker;

import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.packet.interfaces.IWriter;

public interface IJNetOutputWorker {

    /**
     * Add packet or packet group to outputQueue
     *
     * @param writer - sender which will be sent
     */
    void addToQueue(IWriter writer);

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