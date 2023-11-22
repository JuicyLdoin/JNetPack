package net.jnetpack.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JNetServer {

    final int port;

    final NioEventLoopGroup connectionGroup;
    final NioEventLoopGroup workGroup;

    Channel channel;

    public JNetServer() {

        this(8080);

    }

    public JNetServer(int port) {

        this.port = port;
        int threads = 1;

        connectionGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(threads);

    }

    public void start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(connectionGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true);

        channel = bootstrap.bind(port).sync().channel();

    }

    public void stop() {

        channel.close();

        workGroup.shutdownGracefully();
        connectionGroup.shutdownGracefully();

    }
}