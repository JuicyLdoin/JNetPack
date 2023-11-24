package net.jnetpack.server.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetChannelHandler;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * JNet server connection handler
 */
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetServerConnectionHandler extends JNetChannelHandler {

    JNetServerConnection connection;

    /**
     * Read data
     *
     * @param ctx - netty channel
     * @param buf - data
     */
    public void read(@NotNull ChannelHandlerContext ctx, @NotNull JNetBuffer buf) throws Exception {
        int packetId = buf.readInt();
        try {
            Packet packet = JNetOptions.PACKET_REGISTRY.createPacket(packetId);
            packet.read(buf);
            packet.work();

            List<Packet> feedback = packet.feedback();

            if (feedback == null)
                return;

            for (Packet feedbackPacket : feedback) {
                connection.addToQueue(feedbackPacket);
            }
        } catch (JNetPacketUnregisteredException ignored) {
        }
    }
}