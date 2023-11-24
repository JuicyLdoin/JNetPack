package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.ISender;

/**
 * JNet packet
 */
@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Packet implements ISender, IReader {

    byte id;
    PacketPriority packetPriority;

}