package rpn.gateway.model.packets.processors;

import rpn.gateway.model.packets.Packet;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An abstract class that processes packets in a separate thread.
 * Any children must implement the process(Packet packet) method to process the packets polled from the queue.
 */
public abstract class PacketProcessor implements Runnable {

    /**
     * Indicates the state of the thread, whether the processor is running or not.
     */
    private boolean isRunning = false;

    /**
     * A queue of <code>Packet</code> objects.
     */
    private Queue<Packet> queue = new LinkedList<>();

    /**
     * Queues a given packet for processing.
     * Access to the queue is synchronized so calls may block until the lock is available.
     *
     * @param packet The packet to queue for processing.
     * @return Boolean true or false, whether the packet was successfully queued.
     */
    public boolean queue(Packet packet) {
        boolean added;

        synchronized (this) {
            added = queue.add(packet);
            if (added && !isRunning) {
                new Thread(this).start();
            }
        }

        return added;
    }

    /**
     * Polls a single packet from the head of the queue and returns it. This is to prevent blocking access to the queue
     * whilst the packet is being processed.
     * Access to the queue is synchronized so calls may block until the lock is available.
     *
     * @return The packet at the head of the queue.
     */
    private Packet synchronizedPoll() {
        Packet packet;
        synchronized (this) {
            isRunning = !queue.isEmpty();
            packet = queue.poll();
        }
        return packet;
    }

    /**
     * Continuously polls and passes packets for processing.
     */
    private void process() {
        Packet packet;
        while ((packet = synchronizedPoll()) != null) {
            process(packet);
        }
    }

    /**
     * Called every time a packet needs to be processed. Implemented individually by each child.
     *
     * @param packet The packet to be processed.
     * @return Boolean true or false, whether the packet was successfully processed.
     */
    public abstract boolean process(Packet packet);

    /**
     * Called automatically, when this processor is run in a new thread, and in turn calls the process method.
     */
    @Override
    public void run() {
        process();
    }
}
