package net.jnetpack.serialization.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Class for encoding Boolean arrays
 */
public class BooleanArrayEncoder extends MessageToByteEncoder<Boolean[]> {

    protected void encode(ChannelHandlerContext ctx, Boolean[] values, ByteBuf buf) {
        byte result = 0;

        for (int i = 0; i < 8 && i < values.length; i++)
            if (values[i])
                result |= (1 << i);

        buf.writeByte(result);
    }
}