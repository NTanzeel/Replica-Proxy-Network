package rpn.gateway.net.packets.processors;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.net.packets.Packet;

public class Incoming extends PacketProcessor {

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
