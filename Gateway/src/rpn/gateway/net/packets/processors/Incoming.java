package rpn.gateway.net.packets.processors;

import rpn.gateway.net.packets.Packet;

public class Incoming extends PacketProcessor {

    @Override
    public void process(Packet packet) {
        System.out.print("{opcode: " + packet.getOpCode());
        System.out.print(", size: " + packet.getSize());
        System.out.println(", content: " + packet.readString() + "}");
    }
}
