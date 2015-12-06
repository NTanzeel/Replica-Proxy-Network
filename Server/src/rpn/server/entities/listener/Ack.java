package rpn.server.entities.listener;

/**
 * Represents an acknowledgement from a replica for a request.
 */
public class Ack {

    private int id;
    private int senderID;

    public Ack(int id, int senderID) {
        this.id = id;
        this.senderID = senderID;
    }

    public int getId() {
        return id;
    }

    public int getSenderID() {
        return senderID;
    }
}
