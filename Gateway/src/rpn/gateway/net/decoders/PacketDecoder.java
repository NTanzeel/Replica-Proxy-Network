package rpn.gateway.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.packets.Packet;

import java.util.List;

/**
 * Extends the <code>ByteToMessageDecoder</code> class which provides a buffer of incoming data and an empty list
 * which the decoded messages should be added to.
 * Responsible for decoding all packets, from client or server.
 */
public class PacketDecoder extends ByteToMessageDecoder {

    /**
     * The connection this decoder is decoding messages on.
     */
    private Connection sender;

    /**
     * Creates an instance uniquely for a single connection.
     *
     * @param sender The connection this decoder is decoding messages on.
     */
    public PacketDecoder(Connection sender) {
        this.sender = sender;
    }

    /**
     * Decodes a stream of bytes to a list of objects. This method is called automatically by Netty whenever data
     * is available inside the buffer.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in  A buffer containing an in-stream of bytes
     * @param out A decoded list of <code>Packet</code> objects from the buffer.
     * @throws Exception Throws an Exception in case of any errors.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8)
            return;

        int size = in.getInt(4);

        if (in.readableBytes() >= size + 8) {
            out.add(new Packet(in.readInt(), in.readInt(), in.readBytes(size), sender));
        }
    }
}
