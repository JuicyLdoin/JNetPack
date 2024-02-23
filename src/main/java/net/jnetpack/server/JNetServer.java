package net.jnetpack.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.event.interfaces.IEventHandler;
import net.jnetpack.exception.JNetServerAlreadyConnectedException;
import net.jnetpack.exception.connection.JNetConnectionAlreadyExistsException;
import net.jnetpack.exception.connection.JNetConnectionNotFoundException;
import net.jnetpack.server.connection.JNetServerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * JNetServer
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JNetServer {

    final int port;

    final NioEventLoopGroup connectionGroup;
    final NioEventLoopGroup workGroup;
    final Map<Integer, JNetServerConnection> connectionMap;
    Channel channel;
    @Getter
    boolean connected;

    @Getter
    final EventHandlerManager eventHandlerManager;

    /**
     * Default constructor
     */
    public JNetServer() {
        this(8080);
    }

    /**
     * Constructor with port
     *
     * @param port - port number
     */
    public JNetServer(int port) {
        this(port, 1);
    }

    /**
     * Constructor with port
     *
     * @param port    - port number
     * @param threads - number of packet read threads
     */
    public JNetServer(int port, int threads) {
        this.port = port;
        connectionGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(threads);
        connectionMap = new HashMap<>();
        connected = false;
        eventHandlerManager = new EventHandlerManager();
    }

    /**
     * Get {@link JNetServerConnection} from ID
     *
     * @param id - connection id
     * @return - {@link JNetServerConnection}
     * @throws JNetConnectionNotFoundException - connection not found
     */
    public JNetServerConnection getConnection(int id) throws JNetConnectionNotFoundException {
        if (!connectionMap.containsKey(id)) {
            throw new JNetConnectionNotFoundException();
        }

        return connectionMap.get(id);
    }

    /**
     * Add {@link JNetServerConnection} to connectionMap
     *
     * @param connection - {@link JNetServerConnection}
     * @throws JNetConnectionAlreadyExistsException - if connection already exists
     */
    public void connect(JNetServerConnection connection) throws JNetConnectionAlreadyExistsException {
        if (connectionMap.containsKey(connection.getConnectionId())) {
            throw new JNetConnectionAlreadyExistsException();
        }

        connectionMap.put(connection.getConnectionId(), connection);
    }

    /**
     * Add {@link JNetServerConnection} to connectionMap and close previous connection with same ID if exists
     *
     * @param connection - {@link JNetServerConnection}
     */
    public void overrideConnect(JNetServerConnection connection) {
        int id = connection.getConnectionId();
        if (connectionMap.containsKey(id)) {
            connectionMap.remove(id).close();
        }

        connectionMap.put(id, connection);
    }

    /**
     * Close {@link JNetServerConnection}
     *
     * @param id - connection id
     */
    public void closeConnection(int id) throws JNetConnectionNotFoundException {
        JNetServerConnection connection = getConnection(id);
        connection.close();
        connectionMap.remove(id);
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
     * @return - {@link JNetServerHandler server handler which will be registered to {@link ChannelPipeline}}
     */
    private JNetServerHandler getServerHandler() {
        return new JNetServerHandler(this);
    }

    /**
     * Starts the server with netty child options
     *
     * @param bootstrap - {@link ServerBootstrap}
     * @throws JNetServerAlreadyConnectedException - server already connected
     * @throws InterruptedException                - interrupted
     */
    public void start(ServerBootstrap bootstrap) throws JNetServerAlreadyConnectedException, InterruptedException {
        if (connected) {
            throw new JNetServerAlreadyConnectedException();
        }

        bootstrap.group(connectionGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel ch) {
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(getServerHandler());
                    }
                });

        channel = bootstrap.bind(port).sync().channel();
        connected = true;
    }

    /**
     * Starts the server
     *
     * @throws JNetServerAlreadyConnectedException - server already connected
     * @throws InterruptedException                - interrupted
     */
    public void start() throws JNetServerAlreadyConnectedException, InterruptedException {
        if (connected) {
            throw new JNetServerAlreadyConnectedException();
        }

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(connectionGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    protected void initChannel(@NotNull Channel ch) {
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(getServerHandler());
                    }
                })
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
        if (!connected) {
            return;
        }

        channel.close();
        workGroup.shutdownGracefully();
        connectionGroup.shutdownGracefully();
        connectionMap.clear();

        connected = false;
    }
}