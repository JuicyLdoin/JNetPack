package net.jnetpack.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.exception.JNetServerAlreadyConnectedException;

/**
 * JNetServer
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JNetServer {

    final int port;

    final NioEventLoopGroup connectionGroup;
    final NioEventLoopGroup workGroup;

    Channel channel;

    boolean connected;

    /**
     * Default constructor
     */
    public JNetServer() {
        this(8080);
    }

    /**
     * Constructor with port
     *
     * @param port the port number
     */
    public JNetServer(int port) {
        this.port = port;
        int threads = 1;
        connectionGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(threads);
        connected = false;
    }

    /**
     * Starts the server
     *
     * @throws JNetServerAlreadyConnectedException - server already connected
     * @throws InterruptedException                - interrupted
     */
    public void start() throws JNetServerAlreadyConnectedException, InterruptedException {
        if (connected)
            throw new JNetServerAlreadyConnectedException();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(connectionGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true);
        channel = bootstrap.bind(port).sync().channel();
        connected = true;
    }

    /**
     * Stops the server
     */
    public void stop() {
        if (!connected)
            return;

        channel.close();
        workGroup.shutdownGracefully();
        connectionGroup.shutdownGracefully();
        connected = false;
    }
}