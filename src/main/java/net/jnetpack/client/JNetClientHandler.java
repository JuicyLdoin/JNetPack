package net.jnetpack.client;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetChannelHandler;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;

/**
 * JNet client handler
 */
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetClientHandler extends JNetChannelHandler {

    JNetClient jNetClient;

    /**
     * Read data
     *
     * @param ctx - netty channel
     * @param buf - data
     */
    protected void read(ChannelHandlerContext ctx, JNetBuffer buf) throws Exception {
        int packetId = buf.readInt();
        try {
            Packet packet = JNetOptions.PACKET_REGISTRY.createPacket(packetId);
            packet.read(buf);
            jNetClient.receivePacket(packet);
        } catch (JNetPacketUnregisteredException ignored) {
        }
    }
}