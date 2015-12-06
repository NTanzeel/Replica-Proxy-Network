package rpn.gateway.model.packets;

import io.netty.buffer.ByteBuf;
import rpn.gateway.model.connection.Connection;

import java.nio.charset.Charset;

/**
 * Represents a collection of data to be relayed in one transmission.
 * A packet may be a request from a client for the primary or a response from the primary for a client.
 */
public class Packet {

    /**
     * The OpCode for the request represented by this packet.
     */
    private int opCode;

    /**
     * The size of the packet.
     */
    private int size;

    /**
     * The data sent as the payload.
     */
    private ByteBuf payload;

    /**
     * A <code>Connection</code> object representing the sender of the request.
     */
    private Connection sender;

    /**
     * Instantiates a new <code>Packet</code> object to represent the request.
     *
     * @param opCode  The OpCode for the request.
     * @param size    The size of the packet.
     * @param payload The data.
     * @param sender  The sender of the request.
     */
    public Packet(int opCode, int size, ByteBuf payload, Connection sender) {
        this.opCode = opCode;
        this.size = size;
        this.payload = payload;
        this.sender = sender;
    }

    /**
     * Gets the OpCode for the request.
     *
     * @return The OpCode for the request represented by this packet.
     */
    public int getOpCode() {
        return opCode;
    }

    /**
     * Gets the size of the packet.
     *
     * @return The size of the of the packet.
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the <code>Connection</code> object representing the sender of the request.
     *
     * @return A <code>Connection</code> object representing the sender of the request.
     */
    public Connection getSender() {
        return sender;
    }

    /**
     * Gets the data in the payload of the packet.
     *
     * @return The buffer containing the data in the packet.
     */
    public ByteBuf getPayload() {
        return payload;
    }

    /**
     * Returns a string to represent the entire contents of this packet.
     *
     * @return A string to represent the entire contents of this packet.
     */
    @Override
    public String toString() {
        return "Packet{" +
                "opCode=" + opCode +
                ", size=" + size +
                ", payload=" + payload.toString(Charset.forName("UTF-8")) +
                '}';
    }
}
