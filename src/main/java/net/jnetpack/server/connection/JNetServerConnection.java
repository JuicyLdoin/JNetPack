package net.jnetpack.server.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketGroup;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.interfaces.IWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
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
}