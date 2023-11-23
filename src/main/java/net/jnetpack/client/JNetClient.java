package net.jnetpack.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.exception.JNetClientAlreadyConnectedException;

import java.net.InetSocketAddress;

/**
 * JNetClient
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JNetClient {

    final String host;
    final int port;

    final NioEventLoopGroup workGroup;

    Channel channel;

    boolean connected;

    /**
     * Default constructor
     */
    public JNetClient() {
        this("localhost", 8080);
    }

    /**
     * Constructor with host
     *
     * @param host - host to which the client will be connected
     */
    public JNetClient(String host) {
        this(host, 8080);
    }

    /**
     * Constructor with host and port
     *
     * @param host - host to which the client will be connected
     * @param port - port to which the client will be connected
     */
    public JNetClient(String host, int port) {
        this.host = host;
        this.port = port;
        workGroup = new NioEventLoopGroup(1);
        connected = false;
    }

    /**
     * Starts the client
     *
     * @throws JNetClientAlreadyConnectedException - client already connected
     * @throws InterruptedException - interrupted
     */
    public void start() throws JNetClientAlreadyConnectedException, InterruptedException {
        if (connected)
            throw new JNetClientAlreadyConnectedException();

        Bootstrap bootstrap = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();
        connected = true;
    }

    /**
     * Stops the client
     */
    public void stop() {
        if (!connected)
            return;

        channel.close();
        workGroup.shutdownGracefully();
        connected = false;
    }
}