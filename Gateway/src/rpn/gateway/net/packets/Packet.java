package rpn.gateway.net.packets;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;

public class Packet {

    private Connection sender;

    private ByteBuf buffer;

    private int recipientId;

    public Packet(Connection sender, ByteBuf buffer) {
        this.sender = sender;
        this.buffer = buffer;

        if (this.sender.getAttribute("type").equals("CLIENT")) {
            this.recipientId = buffer.readInt();
        } else
            this.recipientId = 0;
    }

    public Connection getSender() {
        return sender;
    }

    public Connection getRecipient() {
        if (this.sender.getAttribute("type").equals("CLIENT")) {
            return ConnectionHandler.getInstance().getClient(recipientId);
        } else {
            return ConnectionHandler.getInstance().getServer(recipientId);
        }
    }
    public int readInt() {
        return buffer.readInt();
    }

    public String readString() {
        String s = "";

        for (char c = (char) buffer.readByte(); c != '\n' && buffer.isReadable(); c = (char) buffer.readByte()) {
            s += c;
        }

        return s;
    }
}
