package net.jnetpack;

import net.jnetpack.packet.registry.PacketRegistry;

public class JNetOptions {

    public static int CONNECTION_THREADS = 4;
    public static PacketRegistry PACKET_REGISTRY = new PacketRegistry("net.jnetpack.packet.defaults");

    public static void extendPacketRegistry(String packageName) {
        PACKET_REGISTRY.merge(new PacketRegistry(packageName));
    }
}