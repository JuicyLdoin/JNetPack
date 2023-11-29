package net.jnetpack.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetChannelHandler;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.JNetClientAlreadyConnectedException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.interfaces.IWriter;
import net.jnetpack.worker.JNetInputWorker;
import net.jnetpack.worker.JNetOutputWorker;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

/**
 * JNetClient
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JNetClient {

    final String host;
    final int port;

    final NioEventLoopGroup workGroup;

    JNetOutputWorker outputWorker;
    JNetInputWorker inputWorker;

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
        workGroup = new NioEventLoopGroup(JNetOptions.CLIENT_THREADS);
        connected = false;
    }

    /**
     * Add {@link IWriter} to {@link JNetOutputWorker}
     *
     * @param writer - target writer
     */
    public void addToQueue(IWriter writer) {
        outputWorker.addToQueue(writer);
    }

    /**
     * Receive packet and add that to {@link JNetInputWorker}
     *
     * @param packet - target packet
     */
    public void receivePacket(Packet packet) {
        inputWorker.addToQueue(packet);
    }

    /**
     * Starts the client
     *
     * @throws JNetClientAlreadyConnectedException - client already connected
     * @throws InterruptedException                - interrupted
     */
    public void start() throws JNetClientAlreadyConnectedException, InterruptedException {
        if (connected)
            throw new JNetClientAlreadyConnectedException();

        JNetClient jNetClient = this;
        Bootstrap bootstrap = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel ch) {
                        ChannelPipeline cp = ch.pipeline();
                        JNetChannelHandler handler = new JNetClientHandler(jNetClient);
                        cp.addLast(handler);

                        ChannelHandlerContext context = cp.context(handler);
                        outputWorker = new JNetOutputWorker(context);
                        inputWorker = new JNetInputWorker(context, outputWorker);
                    }
                })
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
        outputWorker.close();
        inputWorker.close();

        connected = false;
    }
}