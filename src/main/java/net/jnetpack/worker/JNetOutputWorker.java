package net.jnetpack.worker;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.event.packet.PacketWriteEvent;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.common.ExceptionPacket;
import net.jnetpack.packet.interfaces.IWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * JNet output writers queue
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetOutputWorker extends Thread implements IJNetOutputWorker {

    ChannelHandlerContext channel;
    PriorityBlockingQueue<IWriter> outputQueue;

    EventHandlerManager eventHandlerManager;

    /**
     * JNetOutputWorker constructor
     *
     * @param channel             - {@link ChannelHandlerContext netty channel context}
     * @param eventHandlerManager - {@link EventHandlerManager JNet handler manager}
     */
    public JNetOutputWorker(ChannelHandlerContext channel, EventHandlerManager eventHandlerManager) {
        this.channel = channel;
        this.eventHandlerManager = eventHandlerManager;
        outputQueue = new PriorityBlockingQueue<>(50, Comparator.comparingInt(writer -> writer.getPacketPriority().getId()));
        start();
    }

    @Override
    public void addToQueue(IWriter writer) {
        outputQueue.add(writer);
    }

    @Override
    public void callEvent(IEvent event) {
        eventHandlerManager.callEvent(event);
    }

    /**
     * Thread run implementation
     * Send {@link IWriter} from outputQueue
     */
    @Override
    public void run() {
        while (channel.channel().isOpen()) {
            try {
                IWriter writer = outputQueue.take();
                JNetBuffer buffer = new JNetBuffer(channel.alloc().buffer());

                if (writer instanceof Packet packet) {
                    callEvent(new PacketWriteEvent(packet, buffer));
                }

                writer.write(buffer);
                channel.flush();
            } catch (Exception exception) {
                try {
                    ExceptionPacket exceptionPacket = (ExceptionPacket) JNetOptions.PACKET_REGISTRY.createPacket(-1);
                    exceptionPacket.setMessage(exception.getMessage());
                    addToQueue(exceptionPacket);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (ClassCastException castException) {
                    throw new ClassCastException("The default package with ID -1 should be ExceptionPacket");
                } catch (JNetPacketUnregisteredException unregisteredException) {
                    throw new JNetPacketUnregisteredException("ExceptionPacket with id -1 unregistered");
                }
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        outputQueue.clear();
        interrupt();
    }
}