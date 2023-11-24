package net.jnetpack.serialization.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Class for encoding Long values in variable-length format
 */
public class VarLongEncoder extends MessageToByteEncoder<Long> {

    protected void encode(ChannelHandlerContext ctx, Long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;

            if (value != 0) {
                temp |= 0b10000000;
            }

            buf.writeByte(temp);
        } while (value != 0);
    }
}