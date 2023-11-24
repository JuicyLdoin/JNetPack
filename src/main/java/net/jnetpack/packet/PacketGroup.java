package net.jnetpack.packet;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.packet.interfaces.IWriter;
import net.jnetpack.packet.registry.PacketRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Group of {@link Packet JNet packets} which will be sent it all at a time
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketGroup implements IWriter {

    @Getter
    PacketPriority packetPriority;
    PriorityQueue<Packet> queue;

    /**
     * Default constructor
     */
    public PacketGroup(PacketRegistry packetRegistry) {
        this(packetRegistry, PacketPriority.MEDIUM);
    }

    /**
     * Constructor with {@link PacketPriority}
     *
     * @param packetRegistry - JNet {@link PacketRegistry}
     * @param packetPriority - the priority that will be used to send the group
     */
    public PacketGroup(PacketRegistry packetRegistry, PacketPriority packetPriority) {
        this.packetPriority = packetPriority;
        queue = new PriorityQueue<>(Comparator.comparing(packet -> packetRegistry.getId(packet.getClass())));
    }

    /**
     * Constructor with {@link PacketPriority} and {@link Packet JNet packets} array
     *
     * @param packetPriority - the priority that will be used to send the group
     * @param packetRegistry - JNet {@link PacketRegistry}
     * @param packets        - array of {@link Packet JNet packets} which will be added instantly to queue
     */
    public PacketGroup(PacketPriority packetPriority, PacketRegistry packetRegistry, Packet... packets) {
        this(packetRegistry, packetPriority);
        queue.addAll(List.of(packets));
    }

    /**
     * Add a packet to queue
     *
     * @param packet - packet which will be added to queue
     * @return - self
     */
    public PacketGroup add(Packet packet) {
        queue.add(packet);
        return this;
    }

    /**
     * Sent all packets to netty {@link ByteBuf}
     *
     * @param buf - netty {@link ByteBuf}
     */
    public void write(ByteBuf buf) {
        Packet packet;
        while ((packet = queue.poll()) != null) {
            packet.write(buf);
        }
    }
}