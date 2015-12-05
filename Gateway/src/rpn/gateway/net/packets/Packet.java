package rpn.gateway.net.packets;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;

import java.nio.charset.Charset;

public class Packet {


    private int opCode;

    private int size;

    private ByteBuf payload;

    private Connection sender;

    public Packet(int opCode, int size, ByteBuf payload, Connection sender) {
        this.opCode = opCode;
        this.size = size;
        this.payload = payload;
        this.sender = sender;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getSize() {
        return size;
    }

    public Connection getSender() {
        return sender;
    }

    public int readByte() {
        return payload.readByte();
    }

    public int readInt() {
        return payload.readInt();
    }

    public String readString() {
        String s = "";

        for (char c = (char) payload.readByte(); c != '\n' && payload.isReadable(); c = (char) payload.readByte()) {
            s += c;
        }

        return s;
    }

    public ByteBuf getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "opCode=" + opCode +
                ", size=" + size +
                ", payload=" + payload.toString(Charset.forName("UTF-8")) +
                '}';
    }
}
