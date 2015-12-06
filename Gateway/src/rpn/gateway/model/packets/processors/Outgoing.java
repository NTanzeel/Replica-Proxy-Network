package rpn.gateway.model.packets.processors;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.model.packets.Packet;

import java.util.NoSuchElementException;

/**
 * Extends the abstract <code>PacketProcessor</code> class.
 * Responsible for processing all outgoing, server to client, packets.
 */
public class Outgoing extends PacketProcessor {

    /**
     * Called every time a packet needs to be processed. Processes a given packet.
     * This implementation of the packet processor takes an incoming packet and relays it to the primary.
     *
     * @param packet The packet to be processed.
     * @return Boolean true or false, whether the packet was successfully processed.
     */
    @Override
    public boolean process(Packet packet) {

        int id = packet.getPayload().readInt();

        String host = Integer.toString(packet.getPayload().readInt());
        for (int i = 0; i < 3; i++)
            host += "." + Integer.toString(packet.getPayload().readInt());

        if (ConnectionHandler.getInstance().clientExists(id)) {

            try {
                Connection client = ConnectionHandler.getInstance().getClient(id);

                if (client.isActive() && !client.getAttribute("host").equals(host))
                    throw new NoSuchElementException();

                ByteBuf out = client.getChannel().alloc().buffer();

                out.writeInt(packet.getPayload().readableBytes());

                out.writeBytes(packet.getPayload());

                client.getChannel().writeAndFlush(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
