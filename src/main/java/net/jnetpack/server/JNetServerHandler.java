package net.jnetpack.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.defaults.ConnectPacket;
import net.jnetpack.server.connection.JNetServerConnection;
import net.jnetpack.server.connection.JNetServerConnectionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * JNet server handler
 */
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    JNetServer jNetServer;

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

            if (packet instanceof ConnectPacket connectPacket) {
                packet.read(buf);

                JNetServerConnection connection = new JNetServerConnection(ctx, connectPacket.getId());
                jNetServer.connect(connection);

                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addLast(new JNetServerConnectionHandler(connection));
                pipeline.remove(this);
            }
        } catch (JNetPacketUnregisteredException ignored) {}
    }
}