package rpn.server.model.requests;

import io.netty.buffer.ByteBuf;

public interface RequestProcessor {

    ByteBuf process(Request request);
}
