package net.jnetpack.event.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.packet.Packet;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketEvent implements IEvent {

    Packet packet;

}