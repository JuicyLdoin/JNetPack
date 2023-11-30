package net.jnetpack.event.connection;

import net.jnetpack.server.connection.JNetServerConnection;

public class ConnectionConnectEvent extends ConnectionEventCancellable {

    public ConnectionConnectEvent(JNetServerConnection connection) {
        super(connection);
    }
}