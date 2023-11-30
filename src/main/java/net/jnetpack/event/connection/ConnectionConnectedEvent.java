package net.jnetpack.event.connection;

import net.jnetpack.server.connection.JNetServerConnection;

public class ConnectionConnectedEvent extends ConnectionEvent {

    public ConnectionConnectedEvent(JNetServerConnection connection) {
        super(connection);
    }
}