package net.jnetpack.serialization.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Class for decoding Long values in variable-length format
 */
public class VarLongDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
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

        out.add(result);
    }
}