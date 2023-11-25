package net.jnetpack.packet.common;

import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = -1, priority = PacketPriority.HIGH)
public class ExceptionPacket extends MessagePacket {

    public ExceptionPacket(PacketPriority packetPriority) {
        super(packetPriority);
    }

    public ExceptionPacket(PacketPriority packetPriority, String message) {
        super(packetPriority, message);
    }
}