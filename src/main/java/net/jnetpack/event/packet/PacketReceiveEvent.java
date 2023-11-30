package net.jnetpack.event.packet;

import net.jnetpack.packet.Packet;

public class PacketReceiveEvent extends PacketEventCancellable {

    public PacketReceiveEvent(Packet packet) {
        super(packet);
    }
}