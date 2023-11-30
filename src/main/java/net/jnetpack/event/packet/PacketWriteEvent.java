package net.jnetpack.event.packet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketWriteEvent extends PacketEvent {

    JNetBuffer buffer;

    public PacketWriteEvent(Packet packet, JNetBuffer buffer) {
        super(packet);
        this.buffer = buffer;
    }
}