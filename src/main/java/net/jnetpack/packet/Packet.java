package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.IWriter;

import java.util.List;

/**
 * JNet packet
 */
@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Packet implements IWriter, IReader {

    int packetId;
    PacketPriority packetPriority;
    boolean[] options;

    public void work() {
    }

    public List<Packet> feedback() {
        return null;
    }

    public void write(JNetBuffer buf) {
        buf.writeVarInt(packetId);
        buf.writeBooleanArray(options);
    }
}