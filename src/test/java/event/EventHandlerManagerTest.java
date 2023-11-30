package event;

import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.annotation.EventHandler;
import net.jnetpack.event.interfaces.IEventHandler;
import net.jnetpack.event.packet.PacketReceiveEvent;
import net.jnetpack.event.packet.PacketSendEvent;
import net.jnetpack.packet.registry.PacketRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class EventHandlerManagerTest implements IEventHandler {

    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PacketRegistry packetRegistry = new PacketRegistry("net.jnetpack.packet.common");
        EventHandlerManager eventHandlerManager = new EventHandlerManager();
        eventHandlerManager.registerHandler(this);

        eventHandlerManager.callEvent(new PacketReceiveEvent(packetRegistry.createPacket(0)));
        eventHandlerManager.callEvent(new PacketSendEvent(packetRegistry.createPacket(1)));
    }

    @EventHandler
    public void handlePacketReceive(PacketReceiveEvent event) {
        System.out.println("receive");
        System.out.println(event.getPacket().getClass().getName());
        System.out.println(event.getPacket().getPacketId());
    }

    @EventHandler
    public void handlePacketSend(PacketSendEvent event) {
        System.out.println("send");
        System.out.println(event.getPacket().getClass().getName());
        System.out.println(event.getPacket().getPacketId());
    }
}