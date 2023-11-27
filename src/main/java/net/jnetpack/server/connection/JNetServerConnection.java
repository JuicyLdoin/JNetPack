package net.jnetpack.server.connection;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketGroup;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.common.ExceptionPacket;
import net.jnetpack.packet.interfaces.IWriter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * JNetServerConnection class which sends and accepts JNet packets
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetServerConnection extends Thread {

    ChannelHandlerContext channel;

    @Getter
    int connectionId;
    PriorityBlockingQueue<IWriter> queue;

    ExecutorService executor;

    /**
     * JNetServerConnection constructor
     *
     * @param channel      - netty channel context
     * @param connectionId - connection id
     */
    public JNetServerConnection(@NotNull ChannelHandlerContext channel, int connectionId) {
        this.channel = channel;
        this.connectionId = connectionId;
        queue = new PriorityBlockingQueue<>(50, Comparator.comparingInt(writer -> writer.getPacketPriority().getId()));
        executor = Executors.newFixedThreadPool(JNetOptions.CONNECTION_THREADS);
    }

    /**
     * Add packet or packet group to queue
     *
     * @param writer - sender which will be sent
     */
    public void addToQueue(IWriter writer) {
        queue.add(writer);
    }

    /**
     * Packet work
     * If packet is async (options[0]) - send to executor
     *
     * @param packet - target packet
     */
    public void work(Packet packet) {
        boolean[] options = packet.getOptions();
        boolean async = options[0];
        boolean feedback = options[1];
        if (async) {
            CompletableFuture.runAsync(packet::work, executor)
                    .thenAccept(unused -> {
                        if (feedback) {
                            workFeedback(packet);
                        }
                    });
        } else {
            packet.work();
            if (feedback) {
                workFeedback(packet);
            }
        }
    }

    /**
     * Packet work
     *
     * @param packet - target packet
     */
    private void workFeedback(Packet packet) {
        List<Packet> feedback = packet.feedback();

        if (feedback == null)
            return;

        feedback.forEach(packet1 -> packet1.setNeedFeedback(false));
        addToQueue(new PacketGroup(PacketPriority.HIGH, JNetOptions.PACKET_REGISTRY, feedback));
    }

    /**
     * Thread run implementation
     * Send IWriter from queue
     */
    public void run() {
        while (channel.channel().isOpen()) {
            try {
                IWriter writer = queue.take();
                writer.write(new JNetBuffer(channel.alloc().buffer()));
                channel.flush();
            } catch (Exception exception) {
                try {
                    ExceptionPacket exceptionPacket = (ExceptionPacket) JNetOptions.PACKET_REGISTRY.createPacket(-1);
                    exceptionPacket.setMessage(exception.getMessage());
                    addToQueue(exceptionPacket);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (ClassCastException castException) {
                    throw new ClassCastException("The default package with ID -1 should be ExceptionPacket");
                } catch (JNetPacketUnregisteredException unregisteredException) {
                    throw new JNetPacketUnregisteredException("ExceptionPacket with id -1 unregistered");
                }
                exception.printStackTrace();
            }
        }
    }

    /**
     * Shutdown connection
     */
    public void close() {
        channel.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            queue.clear();
            executor.shutdown();
            interrupt();
        });
    }
}