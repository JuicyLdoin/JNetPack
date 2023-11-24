package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * {@link Packet JNet packets} priorities
 */
@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum PacketPriority {

    HIGH((byte) 0),
    MEDIUM((byte) 1),
    LOW((byte) 2);

    byte id;

}