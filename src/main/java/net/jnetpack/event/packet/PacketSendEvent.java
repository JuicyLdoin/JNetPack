package net.jnetpack.event.packet;

import net.jnetpack.packet.Packet;

public class PacketSendEvent extends PacketEventCancellable {

    public PacketSendEvent(Packet packet) {
        super(packet);
    }
}