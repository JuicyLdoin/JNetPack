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

    Map<Class<? extends Packet>, Integer> idPacketMap;
    Map<Integer, Class<? extends Packet>> packetMap;
    Map<Integer, PacketPriority> priorityMap;
    Map<Integer, boolean[]> optionsMap;

    /**
     * Default constructor
     *
     * @param packageName - name of the package from which packets will be taken and registered
     */
    public PacketRegistry(String packageName) {
        idPacketMap = new HashMap<>();
        packetMap = new HashMap<>();
        priorityMap = new HashMap<>();
        optionsMap = new HashMap<>();

        new ReflectionUtil().getClassesImplement(packageName, Packet.class).forEach(packetClass -> {
            JNetPacket jNetPacket = packetClass.getAnnotation(JNetPacket.class);

            if (jNetPacket == null) {
                return;
            }

            register(jNetPacket.id(), jNetPacket.priority(), jNetPacket.options(), packetClass);
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
    public Packet createPacket(int id) throws JNetPacketUnregisteredException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        return get(id).getConstructor(int.class, PacketPriority.class, boolean[].class).newInstance(id, priorityMap.get(id), optionsMap.get(id));
    }

    /**
     * Gets a packet class from packet id
     *
     * @param id - packet id
     * @return - packet class
     * @throws JNetPacketUnregisteredException - if packet isn`t registered
     */
    protected Class<? extends Packet> get(int id) throws JNetPacketUnregisteredException {
        if (!packetMap.containsKey(id)) {
            throw new JNetPacketUnregisteredException();
        }

        return packetMap.get(id);
    }

    /**
     * Gets a packet id from packet class
     *
     * @param clazz - packet class
     * @return - packet id
     * @throws JNetPacketUnregisteredException - if packet isn`t registered
     */
    public int getId(Class<? extends Packet> clazz) throws JNetPacketUnregisteredException {
        if (!idPacketMap.containsKey(clazz)) {
            throw new JNetPacketUnregisteredException();
        }

        return idPacketMap.get(clazz);
    }

    /**
     * Register a packet
     *
     * @param id             - packet id which will be registered
     * @param packetPriority - packet priority which will be registered
     * @param options        - packet options which will be registered
     * @param packet         - packet class which will be registered
     */
    public void register(int id, PacketPriority packetPriority, boolean[] options, Class<? extends Packet> packet) {
        idPacketMap.put(packet, id);
        packetMap.put(id, packet);
        priorityMap.put(id, packetPriority);
        optionsMap.put(id, options);
    }

    /**
     * Unregister a packet
     *
     * @param id - packet id which will be unregistered
     */
    public void unregister(int id) {
        idPacketMap.remove(get(id));
        packetMap.remove(id);
        priorityMap.remove(id);
        optionsMap.remove(id);
    }

    /**
     * Unregister all packets
     */
    public void clear() {
        idPacketMap.clear();
        packetMap.clear();
        priorityMap.clear();
        optionsMap.clear();
    }

    /**
     * Merge current registry with other
     * Register all packets from other packet registry to current
     *
     * @param packetRegistry - target {@link PacketRegistry}
     */
    public void merge(PacketRegistry packetRegistry) {
        for (Map.Entry<Class<? extends Packet>, Integer> entry : packetRegistry.idPacketMap.entrySet()) {
            int id = entry.getValue();
            register(id, packetRegistry.priorityMap.get(id), optionsMap.get(id), entry.getKey());
        }
    }
}