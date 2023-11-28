package net.jnetpack.server.connection.worker;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetBuffer;
import net.jnetpack.JNetOptions;
import net.jnetpack.exception.registry.JNetPacketUnregisteredException;
import net.jnetpack.packet.common.ExceptionPacket;
import net.jnetpack.packet.interfaces.IWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * JNet output writers queue
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetOutputWorker extends Thread {

    ChannelHandlerContext channel;
    PriorityBlockingQueue<IWriter> outputQueue;

    /**
     * JNetOutputWorker constructor
     *
     * @param channel - {@link ChannelHandlerContext netty channel context}
     */
    public JNetOutputWorker(ChannelHandlerContext channel) {
        this.channel = channel;
        outputQueue = new PriorityBlockingQueue<>(50, Comparator.comparingInt(writer -> writer.getPacketPriority().getId()));
    }

    /**
     * Add packet or packet group to outputQueue
     *
     * @param writer - sender which will be sent
     */
    public void addToQueue(IWriter writer) {
        outputQueue.add(writer);
    }

    /**
     * Thread run implementation
     * Send {@link IWriter} from outputQueue
     */
    public void run() {
        while (channel.channel().isOpen()) {
            try {
                IWriter writer = outputQueue.take();
                writer.write(new JNetBuffer(channel.alloc().buffer()));
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

    /**
     * Shutdown worker
     */
    public void close() {
        outputQueue.clear();
        interrupt();
    }
}