package net.jnetpack.serialization.decoders.number;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Class for decoding Integer values in variable-length format
 */
public class VarIntDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));
            numRead++;

            if (numRead > 5)
                throw new RuntimeException("VarInt is too big");

        } while ((read & 0b10000000) != 0);

        out.add(result);
    }
}