package net.jnetpack.packet.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = 1)

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePacket extends Packet {

    String message;

    public MessagePacket(PacketPriority packetPriority) {
        super(packetPriority);
    }

    public MessagePacket(PacketPriority packetPriority, String message) {
        super(packetPriority);
        this.message = message;
    }

    @Override
    public void read(JNetBuffer buf) {
        message = buf.readString();
    }

    @Override
    public void write(JNetBuffer buf) {
        buf.writeString(message);
    }
}