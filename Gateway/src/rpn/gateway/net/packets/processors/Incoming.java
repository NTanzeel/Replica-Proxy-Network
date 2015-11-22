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

        int packetSize = packet.getSize();

        out.writeInt((Integer) packet.getSender().getAttribute("id"));

        packetSize += 4;

        String ip = (String )packet.getSender().getAttribute("host");

        for (String s : ip.split("\\.")) {
            out.writeInt(Integer.parseInt(s));
        }

        packetSize += 16;

        out.writeInt(packetSize);

        out.writeBytes(packet.getPayload());

        packet.getSender().getChannel().writeAndFlush(out);

        System.out.print("{opcode: " + packet.getOpCode());
        System.out.print(", size: " + packet.getSize());
        System.out.println(", content: " + packet.readString() + "}");

        return true;
    }
}
