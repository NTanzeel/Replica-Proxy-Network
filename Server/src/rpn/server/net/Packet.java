package rpn.server.net;

import io.netty.buffer.ByteBuf;

public class Packet {

    private int opCode;
    private int size;

    private int clientID;
    private String host;

    private ByteBuf payload;

    public Packet(int opCode, int size, int clientID, String host, ByteBuf payload) {
        this.opCode = opCode;
        this.size = size;
        this.clientID = clientID;
        this.host = host;
        this.payload = payload;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getSize() {
        return size;
    }

    public int getClientID() {
        return clientID;
    }

    public String getHost() {
        return host;
    }

    public ByteBuf getPayload() {
        return payload;
    }
}
