package net.jnetpack.packet.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = 0)

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ConnectPacket extends Packet {

    int id;

    @Override
    public void read(JNetBuffer buf) {
        id = buf.readInt();
    }

    @Override
    public void write(JNetBuffer buf) {
        buf.writeInt(id);
    }
}