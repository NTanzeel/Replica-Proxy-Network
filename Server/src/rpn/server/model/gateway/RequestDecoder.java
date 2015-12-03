package rpn.server.model.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.net.Packet;

import java.util.List;

public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8 || in.readableBytes() < in.getInt(4))
            return;

        int opCode = in.readInt();
        int size = in.readInt();

        int clientID = in.readInt();

        String host = Integer.toString(in.readInt());
        for (int i = 0; i < 3; i++) {
            host += "." + Integer.toString(in.readInt());
        }

        ByteBuf payload = ctx.alloc().buffer(size - 20);

        out.add(new Packet(opCode, size, clientID, host, in.readBytes(payload)));
    }
}
