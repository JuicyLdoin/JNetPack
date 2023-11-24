package net.jnetpack.packet.interfaces;

import io.netty.buffer.ByteBuf;

/**
 * JNet reader
 */
public interface IReader {

    /**
     * Read self from {@link ByteBuf netty ByteBuf}
     *
     * @param buf - {@link ByteBuf netty ByteBuf} which will be readed
     */
    void read(ByteBuf buf);
}