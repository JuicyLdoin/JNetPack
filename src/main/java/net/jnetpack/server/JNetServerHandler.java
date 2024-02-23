package net.jnetpack.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetChannelHandler;
import net.jnetpack.JNetOptions;
import net.jnetpack.event.connection.ConnectionConnectEvent;
import net.jnetpack.event.connection.ConnectionConnectedEvent;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.common.ConnectPacket;
import net.jnetpack.server.connection.JNetServerConnection;
import net.jnetpack.server.connection.JNetServerConnectionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * JNet server handler
 */
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetServerHandler extends JNetChannelHandler {

    JNetServer jNetServer;

    /**
     * Read data
     * If packet is {@link ConnectPacket} handle it and add new {@link JNetServerConnection}
     *
     * @param ctx - netty channel
     * @param buf - data
     */
    public void read(@NotNull ChannelHandlerContext ctx, @NotNull JNetBuffer buf) throws Exception {
        int packetId = buf.readVarInt();
        try {
            Packet packet = JNetOptions.PACKET_REGISTRY.createPacket(packetId);
            if (!(packet instanceof ConnectPacket connectPacket)) {
                return;
            }

            packet.read(buf);

            JNetServerConnection connection = new JNetServerConnection(ctx, connectPacket.getId(), jNetServer.getEventHandlerManager());
            ConnectionConnectEvent connectEvent = new ConnectionConnectEvent(connection);
            jNetServer.callEvent(connectEvent);

            if (connectEvent.isCancelled()) {
                return;
            }

            if (JNetOptions.OVERRIDE_SERVER_CONNECTIONS) {
                jNetServer.overrideConnect(connection);
            } else {
                jNetServer.connect(connection);
            }

            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addLast(getConnectionHandler(connection));
            pipeline.remove(this);

            jNetServer.callEvent(new ConnectionConnectedEvent(connection));
        } catch (JNetPacketUnregisteredException ignored) {
        }
    }

    /**
     * @param connection - {@link JNetServerConnection JNet server connection}
     * @return - {@link JNetServerConnectionHandler packet handler}
     */
    private JNetServerConnectionHandler getConnectionHandler(JNetServerConnection connection) {
        return new JNetServerConnectionHandler(connection);
    }
}