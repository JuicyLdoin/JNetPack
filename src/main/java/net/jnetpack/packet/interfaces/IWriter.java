package net.jnetpack.packet.interfaces;

import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.PacketPriority;

/**
 * JNet writer
 */
public interface IWriter {

    /**
     * Write a self to {@link JNetBuffer}
     *
     * @param buf - {@link JNetBuffer} in which writer will be written
     */
    void write(JNetBuffer buf);

    /**
     * Return packet priority
     *
     * @return - {@link PacketPriority JNet PacketPriority}
     */
    PacketPriority getPacketPriority();
}