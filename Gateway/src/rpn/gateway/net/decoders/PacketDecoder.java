package rpn.gateway.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.packets.Packet;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private Connection sender;

    public PacketDecoder(Connection sender) {
        this.sender = sender;
    }

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
