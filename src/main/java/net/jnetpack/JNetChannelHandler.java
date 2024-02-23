package net.jnetpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * JNet channel handler which extends {@link SimpleChannelInboundHandler netty handler} and uses {@link JNetBuffer}
 */
public abstract class JNetChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        read(ctx, JNetOptions.BUF_FUNCTION.apply(buf));
    }

    protected abstract void read(ChannelHandlerContext ctx, JNetBuffer buf) throws Exception;
}