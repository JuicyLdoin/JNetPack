package packet.registry;

import net.jnetpack.packet.registry.PacketRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class PacketRegistryTest {

    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PacketRegistry packetRegistry = new PacketRegistry("packet.registry");
        System.out.println(packetRegistry.createPacket((byte) 0).getPacketPriority().name());
    }
}