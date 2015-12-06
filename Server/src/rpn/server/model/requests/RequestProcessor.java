package rpn.server.model.requests;

import io.netty.buffer.ByteBuf;

/**
 * The interface for a packet processor. Each different packet opcode will have its own processor.
 */
public interface RequestProcessor {

    ByteBuf process(Request request);
}
