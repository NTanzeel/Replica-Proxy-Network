package rpn.server.model.responses;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

public class Response {

    private final int id;
    private final int size;
    private final int recipientID;
    private final String recipient;
    private final ByteBuf payload;

    private final ArrayList<Integer> required = new ArrayList<>();

    public Response(int id, int recipientID, String recipient, ByteBuf payload) {
        this.id = id;
        this.size = 20 + payload.readableBytes();
        this.recipientID = recipientID;
        this.recipient = recipient;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public int getRecipientID() {
        return recipientID;
    }

    public String getRecipient() {
        return recipient;
    }

    public ByteBuf getPayload() {
        return payload;
    }

    public void require(Integer id) {
        required.add(id);
    }

    public boolean acknowledge(Integer id) {
        required.remove(id);
        return isAcknowledged();
    }

    public boolean isAcknowledged() {
        return required.isEmpty();
    }
}
