package rpn.server.model.primary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.server.service.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class PrimaryRequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4 || in.readableBytes() < in.getInt(0))
            return;

        out.add(in.readBytes(in.readInt()));
    }
}
