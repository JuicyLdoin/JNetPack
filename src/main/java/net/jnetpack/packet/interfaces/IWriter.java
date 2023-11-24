package net.jnetpack.packet.interfaces;

import net.jnetpack.JNetBuffer;

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
}