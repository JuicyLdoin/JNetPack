package net.jnetpack;

import io.netty.buffer.ByteBuf;
import net.jnetpack.packet.registry.PacketRegistry;

import java.util.function.Function;

public class JNetOptions {

    public static int EVENT_HANDLER_THREADS = 2;
    public static int CLIENT_THREADS = 2;
    public static int WORKER_THREADS = 4;
    public static int IO_QUEUE_SIZE = 50;
    public static boolean OVERRIDE_SERVER_CONNECTIONS = false;
    public static PacketRegistry PACKET_REGISTRY = new PacketRegistry("net.jnetpack.packet.defaults");
    public static Function<ByteBuf, ? extends JNetBuffer> BUF_FUNCTION = JNetBuffer::new;

    public static void extendPacketRegistry(String packageName) {
        PACKET_REGISTRY.merge(new PacketRegistry(packageName));
    }
}