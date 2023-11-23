package net.jnetpack.packet.registry;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketRegistry {

    Map<Byte, Packet> packetMap;

    public PacketRegistry() {

        packetMap = new HashMap<>();

    }

    public Packet get(byte id) {

        if (!packetMap.containsKey(id))
            throw new JNetPacketUnregisteredException();

        return packetMap.get(id);

    }

    public void register(byte id, Packet packet) {

        packetMap.put(id, packet);

    }

    public void unregister(byte id, Packet packet) {

        packetMap.remove(id, packet);

    }
}