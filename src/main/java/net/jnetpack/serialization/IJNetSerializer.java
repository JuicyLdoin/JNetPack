package net.jnetpack.serialization;

import io.netty.buffer.ByteBuf;

/**
 * Interface for JNet serialization.
 *
 * @param <T> the type of object to be serialized/deserialized
 */
public interface IJNetSerializer<T> {

    /**
     * Serializes an object into a ByteBuf.
     *
     * @param t   the object to be serialized
     * @param buf the ByteBuf to write to
     */
    void serialize(T t, ByteBuf buf);

    /**
     * Deserializes an object from a ByteBuf.
     *
     * @param buf the ByteBuf to read from
     * @return the deserialized object
     */
    T deserialize(ByteBuf buf);

}