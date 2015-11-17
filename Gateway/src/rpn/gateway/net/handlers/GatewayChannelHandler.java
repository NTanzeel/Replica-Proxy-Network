package rpn.gateway.net.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;

public class GatewayChannelHandler extends ChannelHandlerAdapter {

    /**
     * A connection object representing the channel, wrapped with attributes.
     */
    private Connection connection = null;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (connection != null) {
            ConnectionHandler.getInstance().deregister(connection);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Connection) {
            this.connection = (Connection) msg;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }
}
