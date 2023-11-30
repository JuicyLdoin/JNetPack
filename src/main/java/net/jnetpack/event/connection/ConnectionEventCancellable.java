package net.jnetpack.event.connection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.interfaces.ICancellable;
import net.jnetpack.server.connection.JNetServerConnection;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionEventCancellable extends ConnectionEvent implements ICancellable {

    boolean cancelled;

    public ConnectionEventCancellable(JNetServerConnection connection) {
        super(connection);
        cancelled = false;
    }
}