package packet.registry;

import net.jnetpack.JNetBuffer;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketPriority;
import net.jnetpack.packet.registry.annotation.JNetPacket;

@JNetPacket(id = 0, priority = PacketPriority.LOW)
public class TestPacket extends Packet {

    public TestPacket(PacketPriority packetPriority) {
        super(packetPriority);
    }

    @Override
    public void write(JNetBuffer buf) {

    }

    @Override
    public void read(JNetBuffer buf) {

    }
}