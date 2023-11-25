package net.jnetpack.server.connection;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.interfaces.IWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
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
     * Send packet to work in executor
     *
     * @param packet - target packet
     */
    public void work(Packet packet) {
        executor.submit(() -> {
            packet.work();
            List<Packet> feedback = packet.feedback();

            if (feedback == null)
                return;

            for (Packet feedbackPacket : feedback) {
                addToQueue(feedbackPacket);
            }
        });
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
                // TODO
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