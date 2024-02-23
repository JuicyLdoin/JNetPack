package net.jnetpack.worker;

import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.jnetpack.JNetOptions;
import net.jnetpack.event.EventHandlerManager;
import net.jnetpack.event.interfaces.IEvent;
import net.jnetpack.event.packet.PacketWorkEvent;
import net.jnetpack.packet.Packet;
import net.jnetpack.packet.PacketGroup;
import net.jnetpack.packet.PacketPriority;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * JNet input packets queue
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JNetInputWorker extends Thread {

    ChannelHandlerContext channel;
    JNetOutputWorker outputWorker;

    ExecutorService executor;
    PriorityBlockingQueue<Packet> inputQueue;

    EventHandlerManager eventHandlerManager;

    /**
     * JNetInputWorker constructor
     *
     * @param channel             - {@link ChannelHandlerContext netty channel context}
     * @param outputWorker        - {@link JNetOutputWorker}
     * @param eventHandlerManager - {@link EventHandlerManager JNet handler manager}
     */
    public JNetInputWorker(ChannelHandlerContext channel, JNetOutputWorker outputWorker, EventHandlerManager eventHandlerManager) {
        this.channel = channel;
        this.outputWorker = outputWorker;
        this.eventHandlerManager = eventHandlerManager;
        executor = Executors.newFixedThreadPool(JNetOptions.WORKER_THREADS);
        inputQueue = new PriorityBlockingQueue<>(50, Comparator.comparingInt(packet -> packet.getPacketPriority().getId()));
        start();
    }

    /**
     * Add received packet to inputQueue
     *
     * @param packet - sender which will be read
     */
    public void addToQueue(Packet packet) {
        inputQueue.add(packet);
    }

    /**
     * Call event in {@link #eventHandlerManager}
     *
     * @param event - target event
     */
    public void callEvent(IEvent event) {
        eventHandlerManager.callEvent(event);
    }

    /**
     * Packet work
     * If packet is async (options[0]) - send to executor
     * If packet is having feedback (options[1]) - work with it
     *
     * @param packet - target packet
     */
    public void work(Packet packet) {
        PacketWorkEvent event = new PacketWorkEvent(packet);
        callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        boolean[] options = packet.getOptions();
        boolean async = options[0];
        boolean feedback = options[1];
        if (async) {
            CompletableFuture.runAsync(packet::work, executor)
                    .thenAccept(unused -> {
                        if (feedback) {
                            workFeedback(packet);
                        }
                    });
        } else {
            packet.work();

            if (feedback) {
                workFeedback(packet);
            }
        }
    }

    /**
     * Packet work
     *
     * @param packet - target packet
     */
    private void workFeedback(Packet packet) {
        List<Packet> feedback = packet.feedback();
        if (feedback.isEmpty()) {
            return;
        }

        feedback.forEach(packet1 -> packet1.setNeedFeedback(false));
        outputWorker.addToQueue(new PacketGroup(PacketPriority.HIGH, JNetOptions.PACKET_REGISTRY, feedback));
    }

    /**
     * Thread run implementation
     * Read {@link Packet} from inputQueue
     */
    public void run() {
        while (channel.channel().isOpen()) {
            try {
                work(inputQueue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Shutdown worker
     */
    public void close() {
        inputQueue.clear();
        executor.shutdown();
        interrupt();
    }
}