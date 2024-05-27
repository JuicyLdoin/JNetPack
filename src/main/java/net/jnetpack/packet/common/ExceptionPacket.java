package net.jnetpack.packet.common;

import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = -1, priority = PacketPriority.HIGH, options = {true, true})
public class ExceptionPacket extends MessagePacket {

    public ExceptionPacket(int packetId, PacketPriority packetPriority, boolean[] options) {
        super(packetId, packetPriority, options);
    }

    public ExceptionPacket(int packetId, PacketPriority packetPriority, boolean[] options, String message) {
        super(packetId, packetPriority, options, message);
    }
}