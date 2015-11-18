package rpn.gateway.net.packets;

import rpn.gateway.net.packets.processors.Incoming;
import rpn.gateway.net.packets.processors.Outgoing;
import rpn.gateway.net.packets.processors.PacketProcessor;

public class PacketHandler {

    private static final PacketHandler instance = new PacketHandler();

    public static PacketHandler getInstance() {
        return instance;
    }

    private PacketProcessor incoming = new Incoming();

    private PacketProcessor outgoing = new Outgoing();

    public boolean queue(Packet packet) {
        if (packet.getSender().getAttribute("type").equals("CLIENT")) {
            return incoming.queue(packet);
        } else {
            return outgoing.queue(packet);
        }
    }
}
