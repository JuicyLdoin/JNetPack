package net.jnetpack.packet;

import net.jnetpack.packet.interfaces.IReader;
import net.jnetpack.packet.interfaces.IWriter;

import java.util.PriorityQueue;

/**
 * JNet packet
 */
public abstract class Packet implements IWriter, IReader {

    public void work() {}

    public PriorityQueue<Packet> feedback() {
        return null;
    }
}