package net.jnetpack.packet.interfaces;

import io.netty.buffer.ByteBuf;

/**
 * JNet writer
 */
public interface IWriter {

    /**
     * Write a self to {@link ByteBuf netty ByteBuf}
     *
     * @param buf - {@link ByteBuf netty ByteBuf} in which writer will be written
     */
    void write(ByteBuf buf);
}