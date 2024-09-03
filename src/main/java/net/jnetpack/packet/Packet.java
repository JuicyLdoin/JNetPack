package net.jnetpack.packet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
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

    public Packet() {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = JNetOptions.PACKET_REGISTRY.getPriorityMap().get(packetId);
        this.options = JNetOptions.PACKET_REGISTRY.getOptionsMap().get(packetId);
    }

    public Packet(PacketPriority packetPriority) {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = packetPriority;
        this.options = new boolean[] {true, false, false, false, false, false, false, false};
    }

    public Packet(boolean async) {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = JNetOptions.PACKET_REGISTRY.getPriorityMap().get(packetId);
        this.options = new boolean[] {async, false, false, false, false, false, false, false};
    }

    public Packet(PacketPriority packetPriority, boolean async) {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = packetPriority;
        this.options = new boolean[] {async, false, false, false, false, false, false, false};
    }

    public Packet(boolean async, boolean needFeedback) {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = JNetOptions.PACKET_REGISTRY.getPriorityMap().get(packetId);
        this.options = new boolean[] {async, needFeedback, false, false, false, false, false, false};
    }

    public Packet(PacketPriority packetPriority, boolean async, boolean needFeedback) {
        this.packetId = JNetOptions.PACKET_REGISTRY.getId(getClass());
        this.packetPriority = packetPriority;
        this.options = new boolean[] {async, needFeedback, false, false, false, false, false, false};
    }

    public boolean isAsync() {
        return options[0];
    }

    public boolean isNeedFeedback() {
        return options[1];
    }

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