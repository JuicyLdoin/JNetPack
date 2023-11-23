package net.jnetpack.packet;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Group of {@link Packet JNet packets} which will be sent it all at a time
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketGroup implements ISender {

    @Getter
    PacketPriority packetPriority;
    PriorityQueue<Packet> queue;

    /**
     * Default constructor
     */
    public PacketGroup() {
        this(PacketPriority.MEDIUM);
    }

    /**
     * Constructor with {@link PacketPriority}
     *
     * @param packetPriority - the priority that will be used to send the group
     */
    public PacketGroup(PacketPriority packetPriority) {
        this.packetPriority = packetPriority;
        queue = new PriorityQueue<>(Comparator.comparing(Packet::getPacketPriority));
    }

    /**
     * Constructor with {@link PacketPriority} and {@link Packet JNet packets} array
     *
     * @param packetPriority - the priority that will be used to send the group
     * @param packets - array of {@link Packet JNet packets} which will be added instantly to queue
     */
    public PacketGroup(PacketPriority packetPriority, Packet... packets) {
        this(packetPriority);
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
    public void send(ByteBuf buf) {
        Packet packet;
        while ((packet = queue.poll()) != null)
            packet.send(buf);
    }
}