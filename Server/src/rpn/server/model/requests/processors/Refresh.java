package rpn.server.model.requests.processors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rpn.server.Server;
import rpn.server.model.requests.Request;
import rpn.server.model.requests.RequestProcessor;

public class Refresh implements RequestProcessor {

    @Override
    public ByteBuf process(Request request) {
        String stocks = Server.getInstance().getService().toString();

        return Unpooled.buffer(stocks.length()).writeBytes(stocks.getBytes());
    }
}
