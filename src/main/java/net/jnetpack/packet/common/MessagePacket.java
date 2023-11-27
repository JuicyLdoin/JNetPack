package net.jnetpack.packet.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = 1)

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePacket extends Packet {

    String message;

    public MessagePacket(int packetId, PacketPriority packetPriority, boolean[] options) {
        super(packetId, packetPriority, options);
    }

    public MessagePacket(int packetId, PacketPriority packetPriority, boolean[] options, String message) {
        super(packetId, packetPriority, options);
        this.message = message;
    }

    @Override
    public void read(JNetBuffer buf) {
        message = buf.readString();
    }

    @Override
    public void write(JNetBuffer buf) {
        super.write(buf);
        buf.writeString(message);
    }
}