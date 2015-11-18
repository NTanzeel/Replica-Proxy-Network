package rpn.gateway.net.packets.processors;

import rpn.gateway.net.packets.Packet;

import java.util.LinkedList;
import java.util.Queue;

public abstract class PacketProcessor implements Runnable {

    private boolean isRunning = false;

    private Queue<Packet> queue = new LinkedList<>();

    public boolean queue(Packet packet) {
        boolean added;

        synchronized (this) {
            added = queue.add(packet);

            System.out.println((isRunning ? "" : "not ") + "running");
            if (added && !isRunning) {
                new Thread(this).start();
            }
        }

        return added;
    }

    private Packet synchronizedPoll() {
        Packet packet;
        synchronized (this) {
            isRunning = !queue.isEmpty();
            packet = queue.poll();
        }
        return packet;
    }

    private void process() {
        Packet packet;
        while ((packet = synchronizedPoll()) != null) {
            process(packet);
        }
    }

    public abstract void process(Packet packet);

    @Override
    public void run() {
        process();
    }
}
