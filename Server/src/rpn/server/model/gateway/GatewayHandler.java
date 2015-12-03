package rpn.server.model.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.server.Server;
import rpn.server.model.primary.Primary;


public class GatewayHandler extends ChannelHandlerAdapter {

    private String serverHost;
    private int serverPort;

    public GatewayHandler(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf out = ctx.alloc().buffer(24);

        out.writeInt(1);

        for (String s : serverHost.split("\\.")) {
            out.writeInt(Integer.parseInt(s));
        }

        out.writeInt(serverPort);

        ctx.writeAndFlush(out);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Primary) {
            Server.getInstance().setPrimary((Primary) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }
}
