package net.jnetpack;

import net.jnetpack.packet.registry.PacketRegistry;

public class JNetOptions {

    public static int CLIENT_THREADS = 2;
    public static int WORKER_THREADS = 4;
    public static PacketRegistry PACKET_REGISTRY = new PacketRegistry("net.jnetpack.packet.defaults");

    public static void extendPacketRegistry(String packageName) {
        PACKET_REGISTRY.merge(new PacketRegistry(packageName));
    }
}