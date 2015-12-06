package rpn.server.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.Server;
import rpn.server.model.requests.Request;

import java.util.List;

public class GatewayRequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!Server.getInstance().isPrimary())
            return;

        if (in.readableBytes() < 8 || in.readableBytes() < in.getInt(4))
            return;

        int opCode = in.readInt();
        int size = in.readInt();

        out.add(new Request(opCode, size, in.readBytes(size)));
    }
}
