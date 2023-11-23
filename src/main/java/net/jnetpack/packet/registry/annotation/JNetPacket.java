package net.jnetpack.packet.registry.annotation;

import net.jnetpack.packet.PacketPriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JNetPacket {

    byte id();

    PacketPriority priority() default PacketPriority.MEDIUM;

}