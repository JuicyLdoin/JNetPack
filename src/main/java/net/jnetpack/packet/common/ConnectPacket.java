package net.jnetpack.packet.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

import java.util.concurrent.ThreadLocalRandom;

@JNetPacket(id = 0, priority = PacketPriority.HIGH, options = {true, true})

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ConnectPacket extends Packet {

    int id;

    public ConnectPacket(int id) {
        this.id = id;
    }

    @Override
    public void read(JNetBuffer buf) {
        super.read(buf);
        if (getOptions()[7]) { // set id to random
            id = ThreadLocalRandom.current().nextInt();
        } else {
            id = buf.readVarInt();
        }
    }

    @Override
    public void write(JNetBuffer buf) {
        super.write(buf);
        if (!getOptions()[7]) {
            buf.writeVarInt(id);
        }
    }
}