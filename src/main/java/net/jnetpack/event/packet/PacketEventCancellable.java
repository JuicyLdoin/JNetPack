package net.jnetpack.event.packet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.interfaces.ICancellable;
import net.jnetpack.packet.Packet;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PacketEventCancellable extends PacketEvent implements ICancellable {

    boolean cancelled;

    public PacketEventCancellable(Packet packet) {
        super(packet);
        cancelled = false;
    }
}