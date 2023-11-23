package net.jnetpack.packet.registry.annotation;

import net.jnetpack.packet.PacketPriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for {@link net.jnetpack.packet.Packet JNet packets}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JNetPacket {

    /**
     * Packet id
     *
     * @return - packet id
     */
    byte id();

    /**
     * Packet send priority
     *
     * @return - packet send priority
     */
    PacketPriority priority() default PacketPriority.MEDIUM;

}