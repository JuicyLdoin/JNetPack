package net.jnetpack.event.packet;

import net.jnetpack.packet.Packet;

public class PacketWorkEvent extends PacketEventCancellable {

    public PacketWorkEvent(Packet packet) {
        super(packet);
    }
}