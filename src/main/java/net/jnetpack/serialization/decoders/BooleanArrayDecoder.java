package net.jnetpack.serialization.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Class for decoding Boolean arrays
 */
public class BooleanArrayDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        byte b = buf.readByte();
        Boolean[] result = new Boolean[8];

        for (int i = 0; i < 8; i++)
            result[i] = ((b >> i) & 1) == 1;

        out.add(result);
    }
}