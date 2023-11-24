package net.jnetpack;

import net.jnetpack.packet.registry.PacketRegistry;

public class JNetOptions {

    public static PacketRegistry PACKET_REGISTRY = new PacketRegistry("net.jnetpack.packet.defaults");

    public static void extendPacketRegistry(String packageName) {
        PacketRegistry packetRegistry = new PacketRegistry(packageName);
        packetRegistry.getIdPacketMap().forEach((clazz, id) -> PACKET_REGISTRY.register(id, packetRegistry.getPriority(clazz), clazz));
    }
}