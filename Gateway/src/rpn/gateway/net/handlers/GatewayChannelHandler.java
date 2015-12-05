package rpn.gateway.net.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.gateway.Gateway;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.net.packets.Packet;
import rpn.gateway.net.packets.PacketHandler;

public class GatewayChannelHandler extends ChannelHandlerAdapter {

    /**
     * A connection object representing the channel, wrapped with attributes.
     */
    private Connection connection = null;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (connection != null) {
            try {
                ConnectionHandler.getInstance().deregister(connection);
            } catch (IllegalStateException e) {
                ctx.channel().parent().close();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Connection) {
            this.connection = (Connection) msg;
            Gateway.LOGGER.info("Registered: " + connection.getAttribute("type"));
        } else if (msg instanceof Packet) {
            PacketHandler.getInstance().queue((Packet) msg);
            Gateway.LOGGER.info("Packet Received " + msg.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }
}
