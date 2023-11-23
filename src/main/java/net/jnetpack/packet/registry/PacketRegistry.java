package net.jnetpack.packet.registry;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;
import net.jnetpack.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketRegistry {

    Map<Byte, Class<? extends Packet>> packetMap;
    Map<Byte, PacketPriority> priorityMap;

    public PacketRegistry(String packageName) {

        packetMap = new HashMap<>();
        priorityMap = new HashMap<>();

        new ReflectionUtil().getClassesImplement(packageName, Packet.class).forEach(packetClass -> {

            JNetPacket jNetPacket = packetClass.getAnnotation(JNetPacket.class);

            if (jNetPacket == null)
                return;

            register(jNetPacket.id(), jNetPacket.priority(), packetClass);

        });
    }

    public Packet createPacket(byte id) throws JNetPacketUnregisteredException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        return get(id).getConstructor(Byte.class, PacketPriority.class).newInstance(id, priorityMap.getOrDefault(id, PacketPriority.MEDIUM));

    }

    protected Class<? extends Packet> get(byte id) {

        if (!packetMap.containsKey(id))
            throw new JNetPacketUnregisteredException();

        return packetMap.get(id);

    }

    public void register(byte id, PacketPriority packetPriority, Class<? extends Packet> packet) {

        packetMap.put(id, packet);
        priorityMap.put(id, packetPriority);

    }

    public void unregister(byte id) {

        packetMap.remove(id);
        priorityMap.remove(id);

    }
}