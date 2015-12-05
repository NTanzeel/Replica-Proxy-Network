package rpn.gateway.net.packets.processors;

import io.netty.buffer.ByteBuf;
import rpn.gateway.Gateway;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.net.packets.Packet;

import java.nio.charset.Charset;
import java.util.NoSuchElementException;

public class Outgoing extends PacketProcessor {

    @Override
    public boolean process(Packet packet) {

        int id = packet.getPayload().readInt();

        String host = Integer.toString(packet.getPayload().readInt());
        for (int i = 0; i < 3; i++)
            host +=  "." + Integer.toString(packet.getPayload().readInt());

        if (ConnectionHandler.getInstance().clientExists(id)) {

            try {
                Connection client = ConnectionHandler.getInstance().getClient(id);

                if (client.isActive() && !client.getAttribute("host").equals(host))
                    throw new NoSuchElementException();

                ByteBuf out = client.getChannel().alloc().buffer();

                System.out.println(packet.getPayload().readableBytes());

                out.writeInt(packet.getPayload().readableBytes());

                System.out.println(packet.getPayload().toString(Charset.forName("UTF-8")));

                out.writeBytes(packet.getPayload());

                client.getChannel().writeAndFlush(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
