package rpn.gateway.model.packets;

import rpn.gateway.model.packets.processors.Incoming;
import rpn.gateway.model.packets.processors.Outgoing;
import rpn.gateway.model.packets.processors.PacketProcessor;

/**
 * Responsible for handling and queuing them into their appropriate queue.
 */
public class PacketHandler {

    /**
     * Creates a single instance of the <code>PacketHandler</code> to be used globally.
     */
    private static final PacketHandler instance = new PacketHandler();

    /**
     * Gets a single instance of the <code>PacketHandler</code>.
     *
     * @return The instance of <code>PacketHandler</code>.
     */
    public static PacketHandler getInstance() {
        return instance;
    }

    /**
     * The packet processor to be used for incoming packets.
     */
    private PacketProcessor incoming = new Incoming();

    /**
     * The packet processor to be used for outgoing packets.
     */
    private PacketProcessor outgoing = new Outgoing();

    /**
     * Queues the given packet for processing by the appropriate processor. If the sender of the packet is a client,
     * it is queued into the incoming packets processor. If the sender of the packet is a server, it is queued into
     * the outgoing packet processor.
     *
     * @param packet The packet to be queued for processing.
     * @return Boolean true or false, whether the packet was successfully queued.
     */
    public boolean queue(Packet packet) {
        if (packet.getSender().getAttribute("type").equals("CLIENT")) {
            return incoming.queue(packet);
        } else {
            return outgoing.queue(packet);
        }
    }
}
