package net.jnetpack.packet;

import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.IWriter;

import java.util.List;

/**
 * JNet packet
 */
public abstract class Packet implements IWriter, IReader {

    public void work() {}

    public List<Packet> feedback() {
        return null;
    }
}