package net.jnetpack.packet.interfaces;

import io.netty.buffer.ByteBuf;

/**
 * JNet sender
 */
public interface ISender {

    /**
     * Write a self to {@link ByteBuf netty ByteBuf}
     *
     * @param buf - {@link ByteBuf netty ByteBuf} in which sender will be written
     */
    void send(ByteBuf buf);
}