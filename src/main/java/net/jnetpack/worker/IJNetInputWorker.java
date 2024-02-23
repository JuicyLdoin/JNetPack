package net.jnetpack.worker;

import net.jnetpack.packet.Packet;

public interface IJNetInputWorker extends IJNetWorker<Packet> {

    /**
     * Packet work
     * If packet is async (options[0]) - send to executor
     * If packet is having feedback (options[1]) - work with it
     *
     * @param packet - target packet
     */
    void work(Packet packet);
}