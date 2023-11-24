package net.jnetpack.packet;

import net.jnetpack.JNetOptions;
import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.IWriter;

import java.util.List;

/**
 * JNet packet
 */
public abstract class Packet implements IWriter, IReader {

    public PacketPriority getPacketPriority() {
        return JNetOptions.PACKET_REGISTRY.getPriority(getClass());
    }

    public void work() {
    }

    public List<Packet> feedback() {
        return null;
    }
}