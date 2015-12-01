package old.rpn.server.net;

public class Message {

    private final boolean success;

    private final byte[] message;

    public Message(boolean success, byte[] message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return true;
    }

    public int getInt() {
        return 0;
    }

    public String getString() {
        return "";
    }

    public byte[] getMessage() {
        return message;
    }

}
