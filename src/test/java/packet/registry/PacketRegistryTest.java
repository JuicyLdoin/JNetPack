package packet.registry;

import net.jnetpack.packet.registry.PacketRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class PacketRegistryTest {

    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PacketRegistry packetRegistry = new PacketRegistry("net.jnetpack.packet.common");
        for (int i = -1; i <= 1; i++) {
            System.out.println(packetRegistry.createPacket(i).getClass().getName());
        }
    }
}