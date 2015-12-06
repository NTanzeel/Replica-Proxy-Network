package rpn.server.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.entities.primary.Primary;

import java.util.List;

public class GatewayConnectionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 8 || in.getInt(4) == 0 && in.readableBytes() < 28)
            return;

        boolean success = in.readInt() == 1;

        if (!success) {
            throw new Exception("Unable to initialise a successful connection to the gateway.");
        }

        boolean isPrimary = in.readInt() == 1;

        if (!isPrimary) {
            String host = Integer.toString(in.readInt());
            for (int i = 0; i < 3; i++)
                host +=  "." + Integer.toString(in.readInt());

            int port = in.readInt();
            out.add(new Primary(host, port));
        } else {
            out.add(Boolean.FALSE);
        }

        ctx.pipeline().replace("decoder", "decoder", new GatewayRequestDecoder());
    }
}