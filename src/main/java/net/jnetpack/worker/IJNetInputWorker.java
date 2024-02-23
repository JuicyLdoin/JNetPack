package net.jnetpack.worker;

import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.packet.Packet;

public interface IJNetInputWorker {

    /**
     * Add received packet to inputQueue
     *
     * @param packet - sender which will be read
     */
    void addToQueue(Packet packet);

    /**
     * Call event
     *
     * @param event - target event
     */
    void callEvent(IEvent event);

    /**
     * Packet work
     * If packet is async (options[0]) - send to executor
     * If packet is having feedback (options[1]) - work with it
     *
     * @param packet - target packet
     */
    void work(Packet packet);

    /**
     * Shutdown worker
     */
    void close();
}