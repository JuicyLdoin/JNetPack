package net.jnetpack.serialization;

import io.netty.buffer.ByteBuf;

public interface IJNetSerializer<T> {

    void serialize(T t, ByteBuf buf);

    T deserialize(ByteBuf buf);

}