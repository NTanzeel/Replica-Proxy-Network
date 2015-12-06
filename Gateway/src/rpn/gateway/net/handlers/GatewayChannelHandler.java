package rpn.gateway.net.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.gateway.Gateway;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.model.packets.Packet;
import rpn.gateway.model.packets.PacketHandler;

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
                Gateway.LOGGER.info(connection.getAttribute("type") + " - Status: Disconnected");
            } catch (IllegalStateException e) {
                ctx.channel().parent().close();
                Gateway.LOGGER.severe("Gateway - Status: Disconnected, Reason: " + e.getMessage());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Connection) {
            this.connection = (Connection) msg;
            Gateway.LOGGER.info(connection.getAttribute("type") + " - Status: Connected");
        } else if (msg instanceof Packet) {
            PacketHandler.getInstance().queue((Packet) msg);
            Gateway.LOGGER.info("Gateway - Status: Packet Received, Message: " + msg.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Gateway.LOGGER.severe("Gateway - Status: Disconnected, Reason: " + cause.getMessage());
    }
}
