package rpn.server.replicaserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.model.connection.Replica;

import java.util.List;

public class ConnectionDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4)
            return;

        if (in.readInt() == 1) {
            out.add(new Replica(ctx.channel()));
            ctx.channel().pipeline().replace("decoder", "decoder", new AckDecoder());
        }

    }
}
