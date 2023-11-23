package net.jnetpack.packet;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketGroup implements ISender {

    @Getter
    PacketPriority packetPriority;
    PriorityQueue<Packet> queue;

    public PacketGroup() {

        this(PacketPriority.MEDIUM);

    }

    public PacketGroup(PacketPriority packetPriority) {

        this.packetPriority = packetPriority;

        queue = new PriorityQueue<>(Comparator.comparing(Packet::getPacketPriority));

    }

    public PacketGroup(PacketPriority packetPriority, Packet... packets) {

        this(packetPriority);

        queue.addAll(List.of(packets));

    }

    public PacketGroup add(Packet packet) {

        queue.add(packet);
        return this;

    }

    public void send(ByteBuf buf) {

        Packet packet;

        while ((packet = queue.poll()) != null)
            packet.send(buf);

    }
}