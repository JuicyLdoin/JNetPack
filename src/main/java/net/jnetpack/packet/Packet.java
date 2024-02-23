package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.IWriter;

import java.util.Collections;
import java.util.List;

/**
 * JNet packet
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Packet implements IWriter, IReader {

    final int packetId;
    PacketPriority packetPriority;
    boolean[] options;

    public void setAsync(boolean async) {
        options[0] = async;
    }

    public void setNeedFeedback(boolean need) {
        options[1] = need;
    }

    public void work() {
    }

    public List<Packet> feedback() {
        return Collections.emptyList();
    }

    @Override
    public void read(JNetBuffer buf) {
        options = buf.readBooleanArray();
    }

    @Override
    public void write(JNetBuffer buf) {
        buf.writeVarInt(packetId);
        buf.writeBooleanArray(options);
    }
}