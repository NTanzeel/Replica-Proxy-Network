package rpn.server.net.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.server.Server;
import rpn.server.model.primary.Primary;
import rpn.server.model.requests.Request;
import rpn.server.model.requests.RequestHandler;

public class GatewayChannelHandler extends ChannelHandlerAdapter {

    private String serverHost;
    private int serverPort;

    public GatewayChannelHandler(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    private void initialiseConnection(ChannelHandlerContext ctx) throws Exception {
        ByteBuf out = ctx.alloc().buffer(24);

        out.writeInt(1);

        for (String s : serverHost.split("\\.")) {
            out.writeInt(Integer.parseInt(s));
        }

        out.writeInt(serverPort);

        ctx.writeAndFlush(out);

        Server.LOGGER.info("Gateway - Status: Initialising");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        initialiseConnection(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Server.LOGGER.info("Gateway - Status: Disconnected");
        Server.getInstance().stop();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Primary) {
            Server.getInstance().setPrimary((Primary) msg);
            Server.LOGGER.info("Gateway - Status: Connected, Role: Replica");
        } else if (msg instanceof Boolean) {
            Server.getInstance().setPrimary(null);
            Server.LOGGER.info("Gateway - Status: Connected, Role: Primary");
        } else if (msg instanceof Request) {
            RequestHandler.getInstance().queue((Request) msg);
            Server.LOGGER.info("Gateway - Status: Request Received, Message: " + msg.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Server.LOGGER.severe("Gateway - Status: Disconnected, Reason: " + cause.getMessage());
        Server.getInstance().stop();
    }
}
