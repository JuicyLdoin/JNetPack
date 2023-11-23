package net.jnetpack.serialization.number;

import io.netty.buffer.ByteBuf;
import net.jnetpack.serialization.IJNetSerializer;

/**
 * Class for serializing and deserializing Long values in variable-length format which uses {@link IJNetSerializer}.
 */
public class VarLongSerializer implements IJNetSerializer<Long> {

    public void serialize(Long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;

            if (value != 0)
                temp |= 0b10000000;

            buf.writeByte(temp);
        } while (value != 0);
    }

    public Long deserialize(ByteBuf buf) {
        int numRead = 0;
        long result = 0;
        byte read;

        do {
            read = buf.readByte();
            long value = (read & 0b01111111);
            result |= (value << (7 * numRead));
            numRead++;

            if (numRead > 10)
                throw new RuntimeException("VarLong is too big");

        } while ((read & 0b10000000) != 0);

        return result;
    }
}