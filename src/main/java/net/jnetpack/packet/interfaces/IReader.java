package net.jnetpack.packet.interfaces;

import net.jnetpack.JNetBuffer;

/**
 * JNet reader
 */
public interface IReader {

    /**
     * Read self from {@link JNetBuffer}
     *
     * @param buf - {@link JNetBuffer} which will be readed
     */
    void read(JNetBuffer buf);
}