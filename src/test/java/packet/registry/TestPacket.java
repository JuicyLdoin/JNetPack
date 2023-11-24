package packet.registry;

import io.netty.buffer.ByteBuf;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = 0, priority = PacketPriority.LOW)
public class TestPacket extends Packet {

    @Override
    public void write(ByteBuf buf) {

    }

    @Override
    public void read(ByteBuf buf) {

    }
}