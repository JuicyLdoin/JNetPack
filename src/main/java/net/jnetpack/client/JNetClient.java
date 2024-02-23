package net.jnetpack.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetChannelHandler;
import net.jnetpack.JNetOptions;
import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.event.interfaces.IEventHandler;
import net.jnetpack.exception.JNetClientAlreadyConnectedException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.interfaces.IWriter;
import net.jnetpack.worker.common.JNetInputWorker;
import net.jnetpack.worker.common.JNetOutputWorker;
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
    final EventHandlerManager eventHandlerManager;
    JNetOutputWorker outputWorker;
    JNetInputWorker inputWorker;
    Channel channel;
    @Getter
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
        eventHandlerManager = new EventHandlerManager();
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
     * Register {@link IEventHandler JNet event handler}
     *
     * @param handler - target handlers
     */
    public void registerHandler(IEventHandler handler) {
        eventHandlerManager.registerHandler(handler);
    }

    /**
     * Unregister {@link IEventHandler JNet event handler}
     *
     * @param handler - target handlers
     */
    public void unregisterHandler(IEventHandler handler) {
        eventHandlerManager.unregisterHandler(handler);
    }

    /**
     * Call event in {@link #eventHandlerManager}
     *
     * @param event - target event
     */
    public void callEvent(IEvent event) {
        eventHandlerManager.callEvent(event);
    }

    /**
     * @return - {@link JNetClientHandler client handler which will be registered to {@link ChannelPipeline}}
     */
    private JNetClientHandler getClientHandler() {
        return new JNetClientHandler(this);
    }

    /**
     * Starts the client with netty bootstrap
     *
     * @param bootstrap - {@link Bootstrap}
     * @throws JNetClientAlreadyConnectedException - client already connected
     * @throws InterruptedException                - interrupted
     */
    public void start(Bootstrap bootstrap) throws JNetClientAlreadyConnectedException, InterruptedException {
        if (connected) {
            throw new JNetClientAlreadyConnectedException();
        }

        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel ch) {
                        JNetClient.this.initChannel(ch);
                    }
                });
        channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();
        connected = true;
    }

    /**
     * Starts the client
     *
     * @throws JNetClientAlreadyConnectedException - client already connected
     * @throws InterruptedException                - interrupted
     */
    public void start() throws JNetClientAlreadyConnectedException, InterruptedException {
        if (connected) {
            throw new JNetClientAlreadyConnectedException();
        }

        Bootstrap bootstrap = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel ch) {
                        JNetClient.this.initChannel(ch);
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();
        connected = true;
    }

    /**
     * Initialize netty channel
     *
     * @param channel - {@link Channel netty channel}
     */
    private void initChannel(@NotNull Channel channel) {
        ChannelPipeline cp = channel.pipeline();
        JNetChannelHandler handler = getClientHandler();
        cp.addLast(handler);

        ChannelHandlerContext context = cp.context(handler);
        outputWorker = new JNetOutputWorker(context, eventHandlerManager);
        inputWorker = new JNetInputWorker(context, outputWorker, eventHandlerManager);
    }

    /**
     * Stops the client
     */
    public void stop() {
        if (!connected) {
            return;
        }

        channel.close();
        workGroup.shutdownGracefully();
        outputWorker.close();
        inputWorker.close();

        connected = false;
    }
}