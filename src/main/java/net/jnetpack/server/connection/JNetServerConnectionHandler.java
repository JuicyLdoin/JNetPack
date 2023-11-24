package net.jnetpack.server.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class JNetServerConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    JNetServerConnection connection;

    /**
     * Read data
     *
     * @param ctx - netty channel
     * @param buf - data
     */
    public void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull ByteBuf buf) throws Exception {
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