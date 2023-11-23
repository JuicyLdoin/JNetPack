package net.jnetpack.packet;

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

    /**
     * @return - {@link PacketPriority JNet packet priority} that will be used to send
     */
    PacketPriority getPacketPriority();

}