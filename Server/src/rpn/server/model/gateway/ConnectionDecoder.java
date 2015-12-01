package rpn.server.model.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.model.primary.Primary;

import java.util.List;

public class ConnectionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8 || in.getInt(4) == 0 && in.readableBytes() < 28)
            return;

        int success = in.readInt();
        int isPrimary = in.readInt();

        if (success == 1 && isPrimary == 1) {
            String host = Integer.toString(in.readInt());
            for (int i = 0; i < 3; i++)
                host +=  "." + Integer.toString(in.readInt());

            int port = in.readInt();

            out.add(new Primary(host, port));
        }
    }
}