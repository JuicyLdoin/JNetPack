package net.jnetpack.serialization;

import io.netty.buffer.ByteBuf;

public class BooleanArraySerialize {

    public static void write(boolean[] values, ByteBuf buf) {
        byte result = 0;

        for (int i = 0; i < 8 && i < values.length; i++) {
            if (values[i]) {
                result |= (1 << i);
            }
        }

        buf.writeByte(result);
    }

    public static boolean[] read(ByteBuf buf) {
        byte b = buf.readByte();
        boolean[] result = new boolean[8];

        for (int i = 0; i < 8; i++) {
            result[i] = ((b >> i) & 1) == 1;
        }

        return result;
    }
}