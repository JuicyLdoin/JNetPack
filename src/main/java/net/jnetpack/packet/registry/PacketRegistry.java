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

/**
 * JNet packet registry which contains a packet ids and classes
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PacketRegistry {

    Map<Class<? extends Packet>, Byte> idPacketMap;
    Map<Byte, Class<? extends Packet>> packetMap;
    Map<Byte, PacketPriority> priorityMap;

    /**
     * Default constructor
     *
     * @param packageName - name of the package from which packets will be taken and registered
     */
    public PacketRegistry(String packageName) {
        idPacketMap = new HashMap<>();
        packetMap = new HashMap<>();
        priorityMap = new HashMap<>();

        new ReflectionUtil().getClassesImplement(packageName, Packet.class).forEach(packetClass -> {
            JNetPacket jNetPacket = packetClass.getAnnotation(JNetPacket.class);

            if (jNetPacket == null)
                return;

            register(jNetPacket.id(), jNetPacket.priority(), packetClass);
        });
    }

    /**
     * Create a packet from packet id
     *
     * @param id - packet id
     * @return - packet
     * @throws JNetPacketUnregisteredException - packet not registered
     * @throws NoSuchMethodException           - no such method
     * @throws InvocationTargetException       - invocation target
     * @throws InstantiationException          - instantiation
     * @throws IllegalAccessException          - illegal access
     */
    public Packet createPacket(byte id) throws JNetPacketUnregisteredException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        return get(id).getConstructor(Byte.class, PacketPriority.class).newInstance(id, priorityMap.getOrDefault(id, PacketPriority.MEDIUM));
    }

    /**
     * Gets a packet class from packet id
     *
     * @param id - packet id
     * @return - packet class
     * @throws JNetPacketUnregisteredException - if packet isn`t registered
     */
    protected Class<? extends Packet> get(byte id) throws JNetPacketUnregisteredException {
        if (!packetMap.containsKey(id))
            throw new JNetPacketUnregisteredException();

        return packetMap.get(id);
    }

    /**
     * Gets a packet id from packet class
     *
     * @param clazz - packet class
     * @return - packet id
     * @throws JNetPacketUnregisteredException - if packet isn`t registered
     */
    public byte getId(Class<? extends Packet> clazz) throws JNetPacketUnregisteredException {
        if (!idPacketMap.containsKey(clazz))
            throw new JNetPacketUnregisteredException();

        return idPacketMap.get(clazz);
    }

    /**
     * Register a packet
     *
     * @param id             - packet id which will be registered
     * @param packetPriority - packet priority which will be registered
     * @param packet         - packet class which will be registered
     */
    public void register(byte id, PacketPriority packetPriority, Class<? extends Packet> packet) {
        idPacketMap.put(packet, id);
        packetMap.put(id, packet);
        priorityMap.put(id, packetPriority);
    }

    /**
     * Unregister a packet
     *
     * @param id - packet id which will be unregistered
     */
    public void unregister(byte id) {
        idPacketMap.remove(get(id));
        packetMap.remove(id);
        priorityMap.remove(id);
    }
}