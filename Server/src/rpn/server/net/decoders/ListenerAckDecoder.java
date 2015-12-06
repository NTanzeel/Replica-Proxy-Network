package rpn.server.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.entities.listener.Ack;
import rpn.server.model.replica.Replica;

import java.util.List;

public class ListenerAckDecoder extends ByteToMessageDecoder {

    private Replica replica;

    public ListenerAckDecoder(Replica replica) {
        this.replica = replica;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4)
            return;

        out.add(new Ack(in.readInt(), replica.getId()));
    }
}
