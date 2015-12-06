package rpn.gateway.model.packets.processors;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.model.packets.Packet;

/**
 * Extends the abstract <code>PacketProcessor</code> class.
 * Responsible for processing all incoming, client to server, packets.
 */
public class Incoming extends PacketProcessor {

    /**
     * Called every time a packet needs to be processed. Processes a given packet.
     * This implementation of the packet processor takes an outgoing packet and identifies it's destination.
     * The packet is then relayed to it's intended recipient.
     *
     * @param packet The packet to be processed.
     * @return Boolean true or false, whether the packet was successfully processed.
     */
    @Override
    public boolean process(Packet packet) {
        if (!packet.getSender().isActive() || ConnectionHandler.getInstance().getNoOfServers() == 0)
            return false;

        Connection primary = ConnectionHandler.getInstance().getServer(0);

        if (!primary.isActive())
            return false;

        ByteBuf out = primary.getChannel().alloc().buffer();
        out.writeInt(packet.getOpCode());

        int packetSize = packet.getSize() + 20;

        out.writeInt(packetSize);

        out.writeInt((Integer) packet.getSender().getAttribute("id"));

        String host = (String) packet.getSender().getAttribute("host");

        for (String s : host.split("\\.")) {
            out.writeInt(Integer.parseInt(s));
        }

        out.writeBytes(packet.getPayload());

        primary.getChannel().writeAndFlush(out);

        return true;
    }
}
