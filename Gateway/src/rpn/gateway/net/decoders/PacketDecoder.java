package rpn.gateway.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.net.packets.Packet;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private Connection sender;

    public PacketDecoder(Connection sender) {
        this.sender = sender;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = in.readInt();

        if (in.readableBytes() >= size) {
            out.add(new Packet(sender, in.readBytes(size)));
        }
    }
}
