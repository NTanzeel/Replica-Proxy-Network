package rpn.server.net.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.Message;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        boolean success = byteBuf.readBoolean();

        int length = byteBuf.readableBytes();
        byte message[] = new byte[length];

        list.add(new Message(success, message));
    }
}