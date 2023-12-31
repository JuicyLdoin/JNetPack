package net.jnetpack.packet.registry.annotation;

import net.jnetpack.packet.PacketPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@link net.jnetpack.packet.Packet JNet packets}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JNetPacket {

    /**
     * Packet id
     *
     * @return - packet id
     */
    int id();

    /**
     * Packet send priority
     *
     * @return - packet send priority
     */
    PacketPriority priority() default PacketPriority.MEDIUM;

    /**
     * Packet options
     * <p>
     * 0 - is async
     * 1 - has feedback
     *
     * @return - array of booleans (max work length - 8)
     */
    boolean[] options() default {true, false};

}