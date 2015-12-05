package rpn.server.model.requests.processors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rpn.server.Server;
import rpn.server.model.requests.Request;
import rpn.server.model.requests.RequestProcessor;

import java.nio.charset.Charset;

public class Buy implements RequestProcessor {

    @Override
    public ByteBuf process(Request request) {
        String key = request.getPayload().readBytes(4).toString(Charset.forName("UTF-8"));
        int quantity = request.getPayload().readInt();

        int status = Server.getInstance().getService().buyStock(key, quantity);

        return Unpooled.buffer(8).writeInt(status == 1 ? 1 : 0).writeInt(status);
    }
}
