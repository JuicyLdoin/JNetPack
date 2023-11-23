package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Packet implements ISender {

    final PacketPriority packetPriority;

    protected Packet(PacketPriority packetPriority) {

        this.packetPriority = packetPriority;

    }
}