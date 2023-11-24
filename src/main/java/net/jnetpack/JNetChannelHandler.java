package net.jnetpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class JNetChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        read(ctx, new JNetBuffer(buf));
    }

    protected abstract void read(ChannelHandlerContext ctx, JNetBuffer buf) throws Exception;
}