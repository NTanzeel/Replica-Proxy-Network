package rpn.server.model.requests;

import io.netty.buffer.ByteBuf;

/**
 * Represents a request from the client.
 */
public class Request {

    private int id;

    private int opCode;
    private int size;

    private int senderId;
    private String sender;

    private ByteBuf payload;

    private boolean isReadOnly;

    public Request(int opCode, int size, ByteBuf payload) {
        this.opCode = opCode;
        this.senderId = payload.readInt();

        String sender = Integer.toString(payload.readInt());
        for (int i = 0; i < 3; i++) {
            sender += "." + Integer.toString(payload.readInt());
        }

        size -= 20;

        this.size = size;
        this.sender = sender;
        this.payload = payload;

        this.isReadOnly = !payload.isReadable();

        this.id = (opCode + size + senderId + sender + payload.hashCode() + System.currentTimeMillis()).hashCode();
    }

    public int getId() {
        return id;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSender() {
        return sender;
    }

    public ByteBuf getPayload() {
        return payload;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public String toString() {
        return "Request{" +
                "sender='" + sender + '\'' +
                ", senderId=" + senderId +
                ", size=" + size +
                ", opCode=" + opCode +
                ", id=" + id +
                '}';
    }
}
