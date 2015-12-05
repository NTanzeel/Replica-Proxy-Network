package rpn.server.model.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.model.replica.Replica;

import java.util.List;

public class ListenerConnectionDecoder extends ByteToMessageDecoder {

    private Replica replica;

    public ListenerConnectionDecoder(Replica replica) {
        this.replica = replica;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4)
            return;

        out.add(in.readInt() == 1);
        ctx.channel().pipeline().replace("decoder", "decoder", new ListenerAckDecoder(replica));
    }
}
