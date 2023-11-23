package net.jnetpack.server.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.packet.ISender;
import net.jnetpack.packet.registry.PacketRegistry;
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
    String connectionName;
    PriorityBlockingQueue<ISender> queue;

    @Getter
    PacketRegistry packetRegistry;

    /**
     * JNetServerConnection constructor
     *
     * @param channel - netty channel context
     * @param connectionName - JNet connection name
     * @param packetRegistry - JNet packet registry
     */
    public JNetServerConnection(@NotNull ChannelHandlerContext channel, String connectionName, PacketRegistry packetRegistry) {
        this.channel = channel;
        this.connectionName = connectionName;
        queue = new PriorityBlockingQueue<>(50, Comparator.comparing(ISender::getPacketPriority));
        this.packetRegistry = packetRegistry;
    }

    /**
     * Add packet or packet group to queue
     *
     * @param sender - sender which will be sent
     */
    public void addToQueue(ISender sender) {
        queue.add(sender);
    }

    /**
     * Thread run implementation
     * Send ISender from queue
     */
    public void run() {
        while (channel.channel().isOpen()) {
            try {
                ISender sender = queue.take();
                sender.send(channel.alloc().buffer());
                channel.flush();
            } catch (Exception exception) {
                // TODO
            }
        }
    }
}