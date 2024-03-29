package net.jnetpack.server.connection;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.interfaces.IWriter;
import net.jnetpack.worker.IJNetInputWorker;
import net.jnetpack.worker.IJNetOutputWorker;
import net.jnetpack.worker.common.JNetInputWorker;
import net.jnetpack.worker.common.JNetOutputWorker;
import org.jetbrains.annotations.NotNull;

/**
 * JNetServerConnection class which sends and accepts JNet packets
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetServerConnection {

    ChannelHandlerContext channel;

    @Getter
    int connectionId;

    IJNetOutputWorker outputWorker;
    IJNetInputWorker inputWorker;

    EventHandlerManager eventHandlerManager;

    /**
     * JNetServerConnection constructor
     *
     * @param channel             - netty channel context
     * @param connectionId        - connection id
     * @param eventHandlerManager - {@link EventHandlerManager JNet handler manager}
     */
    public JNetServerConnection(@NotNull ChannelHandlerContext channel, int connectionId, @NotNull EventHandlerManager eventHandlerManager) {
        this.eventHandlerManager = eventHandlerManager;
        this.channel = channel;
        this.connectionId = connectionId;
        outputWorker = new JNetOutputWorker(channel, eventHandlerManager);
        inputWorker = new JNetInputWorker(channel, outputWorker, eventHandlerManager);
    }

    /**
     * JNetServerConnection constructor
     *
     * @param channel             - netty channel context
     * @param connectionId        - connection id
     * @param eventHandlerManager - {@link EventHandlerManager JNet handler manager}
     * @param outputWorker        - {@link IJNetOutputWorker JNet output worker}
     * @param inputWorker         - {@link IJNetInputWorker JNet input worker}
     */
    public JNetServerConnection(@NotNull ChannelHandlerContext channel, int connectionId, @NotNull EventHandlerManager eventHandlerManager,
                                @NotNull IJNetOutputWorker outputWorker, @NotNull IJNetInputWorker inputWorker) {
        this.eventHandlerManager = eventHandlerManager;
        this.channel = channel;
        this.connectionId = connectionId;
        this.outputWorker = outputWorker;
        this.inputWorker = inputWorker;
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
     * Call event in {@link #eventHandlerManager}
     *
     * @param event - target event
     */
    public void callEvent(IEvent event) {
        eventHandlerManager.callEvent(event);
    }

    /**
     * Shutdown connection
     */
    public void close() {
        channel.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            outputWorker.close();
            inputWorker.close();
            eventHandlerManager.close();
        });
    }
}