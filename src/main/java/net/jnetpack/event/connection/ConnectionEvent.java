package net.jnetpack.event.connection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.server.connection.JNetServerConnection;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConnectionEvent implements IEvent {

    JNetServerConnection connection;

}