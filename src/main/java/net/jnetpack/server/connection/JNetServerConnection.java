package net.jnetpack.server.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.interfaces.IWriter;
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
    PriorityBlockingQueue<IWriter> queue;

    @Getter
    PacketRegistry packetRegistry;

    /**
     * JNetServerConnection constructor
     *
     * @param channel        - netty channel context
     * @param connectionName - JNet connection name
     * @param packetRegistry - JNet packet registry
     */
    public JNetServerConnection(@NotNull ChannelHandlerContext channel, String connectionName, PacketRegistry packetRegistry) {
        this.channel = channel;
        this.connectionName = connectionName;
        queue = new PriorityBlockingQueue<>(50, Comparator.comparingInt(writer -> {
            if (writer instanceof Packet packet) {
                return packetRegistry.getPriority(packet.getClass()).ordinal();
            }
            return 1;
        }));
        this.packetRegistry = packetRegistry;
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
                writer.write(channel.alloc().buffer());
                channel.flush();
            } catch (Exception exception) {
                // TODO
            }
        }
    }
}