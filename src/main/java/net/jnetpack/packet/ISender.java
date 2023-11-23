package net.jnetpack.packet;

import io.netty.buffer.ByteBuf;

public interface ISender {

    void send(ByteBuf buf);

}