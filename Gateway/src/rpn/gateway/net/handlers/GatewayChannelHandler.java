package rpn.gateway.net.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.gateway.Gateway;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;
import rpn.gateway.model.packets.Packet;
import rpn.gateway.model.packets.PacketHandler;

/**
 * Extends the <code>ChannelHandlerAdapter</code> provided by Netty and overrides any method necessary.
 * Handles the channel for connections to the gateway. A new instance is created for each connection.
 */
public class GatewayChannelHandler extends ChannelHandlerAdapter {

    /**
     * A connection object representing the channel, wrapped with attributes.
     */
    private Connection connection = null;

    /**
     * This method is called automatically by Netty whenever the connection associated with this handler is dropped.
     * DeRegisters the connection from the <code>ConnectionHandler</code>. If the last server is removed and an
     * <code>IllegalStateException</code> is thrown then the entire Gateway is disconnected.
     * @param ctx @param ctx The channel handler, passed through as a parameter by Netty
     */
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

    /**
     * This method is called automatically by Netty whenever the decoder outputs a message.
     * Determines the type of message. If the message is a new connection then register it, otherwise if the message
     * is a packet then queue it for processing.
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param msg The decoded message output by the decoder.
     */
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

    /**
     * This method is called automatically by Netty whenever the an internal uncaught exception is thrown.
     * As a response, the gateway is shutdown.
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param cause The cause of the exception.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Gateway.LOGGER.severe("Gateway - Status: Disconnected, Reason: " + cause.getMessage());
    }
}
