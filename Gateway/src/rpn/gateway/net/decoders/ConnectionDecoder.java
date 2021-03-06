package rpn.gateway.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;

import java.util.List;

/**
 * Extends the <code>ByteToMessageDecoder</code> class which provides a buffer of incoming data and an empty list
 * which the decoded messages should be added to.
 * Responsible for decoding new incoming connections and outputting the appropriate <code>Connection</code> object.
 */
public class ConnectionDecoder extends ByteToMessageDecoder {

    /**
     * Represents all the possible states of a connection
     */
    private enum ConnectionState {
        Initial,
        Pending,
        Established
    }

    /**
     * The current state of the connection
     */
    private ConnectionState state = ConnectionState.Initial;

    /**
     * Whether the connection is a server or a client
     */
    private boolean isServer = false;

    /**
     * Decodes a stream of bytes to a list of objects. This method is called automatically by Netty whenever data
     * is available inside the buffer.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in  A buffer containing an in-stream of bytes
     * @param out A decoded list of objects from the buffer.
     * @throws Exception Throws an Exception in case of any errors.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state) {
            case Initial:
                initializeConnection(in);
                break;
            case Pending:
                establishConnection(ctx, in, out);
                break;
        }
    }

    /**
     * Initializes a connection by reading the first set of data to determine whether the incoming connection is a
     * client or a server.
     *
     * @param in A buffer containing an in-stream of bytes
     */
    private void initializeConnection(ByteBuf in) {
        if (in.readableBytes() >= 4) {
            isServer = in.readInt() == 1;
            state = ConnectionState.Pending;
        }
    }

    /**
     * Establish a new connection by performing a handshake and outputs the appropriate connection.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in  A buffer containing an in-stream of bytes
     * @param out A decoded list of objects from the buffer.
     */
    public void establishConnection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Connection connection = isServer ? getServer(ctx, in) : getClient(ctx, in);

        if (connection != null) {
            out.add(connection);
            state = ConnectionState.Established;
            ctx.channel().pipeline().replace("decoder", "decoder", new PacketDecoder(connection));
        }
    }

    /**
     * Gets a Connection object representing the servers connection, with various attributes attached, or null if
     * not enough information is available to produce a Connection object.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in  A buffer containing an in-stream of bytes
     * @return A Connection object representing the servers connection or null
     */
    public Connection getServer(ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < 20)
            return null;

        String host = Integer.toString(in.readInt());
        for (int i = 0; i < 3; i++)
            host += "." + Integer.toString(in.readInt());

        int port = in.readInt();

        Connection server = ConnectionHandler.getInstance().register(ctx.channel(), host, port);

        ByteBuf response = ctx.alloc().buffer();

        response.writeInt(1);

        if (!((Boolean) server.getAttribute("isPrimary"))) {
            response.writeInt(0);

            String primaryHost = (String) ConnectionHandler.getInstance().getServer(0).getAttribute("host");
            int primaryPort = (int) ConnectionHandler.getInstance().getServer(0).getAttribute("port");

            for (String s : primaryHost.split("\\.")) {
                response.writeInt(Integer.parseInt(s));
            }

            response.writeInt(primaryPort);

        } else response.writeInt(1);

        writeResponse(ctx, response, false);

        return server;
    }

    /**
     * Gets a Connection object representing the clients connection, with various attributes attached, or null if
     * not enough information is available to produce a Connection object.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in  A buffer containing an in-stream of bytes
     * @return A Connection object representing the clients connection or null
     */
    public Connection getClient(ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < 16)
            return null;

        Connection client = null;

        if (ConnectionHandler.getInstance().getNoOfServers() > 0) {
            String host = Integer.toString(in.readInt());
            for (int i = 0; i < 3; i++)
                host += "." + Integer.toString(in.readInt());

            try {
                client = ConnectionHandler.getInstance().register(ctx.channel(), host);
                writeResponse(ctx, 1, 1, false);
            } catch (IndexOutOfBoundsException e) {
                writeResponse(ctx, 0, 1, true);
            }
        } else {
            writeResponse(ctx, 0, 2, true);
        }

        return client;
    }

    /**
     * Writes a success or failure code back to the client/server and, if requested, closes the connection upon
     * completion.
     *
     * @param ctx        The channel handler, passed through as a parameter by Netty
     * @param success    Whether a connection has successfully been established
     * @param status     The status of the connection, usually a 1 for success or an integer error code for failure
     * @param forceClose Whether the connection should be forcefully closed upon write completion
     */
    private void writeResponse(ChannelHandlerContext ctx, int success, int status, boolean forceClose) {
        ByteBuf response = ctx.alloc().buffer().writeInt(success).writeInt(status);
        writeResponse(ctx, response, forceClose);
    }

    /**
     * Writes the specified buffer to the channel and, if requested, closes the connection upon completion.
     *
     * @param ctx        The channel handler, passed through as a parameter by Netty.
     * @param response   A buffer containing the response to be written to the channel.
     * @param forceClose Whether the connection should be forcefully closed upon write completion
     */
    private void writeResponse(ChannelHandlerContext ctx, ByteBuf response, boolean forceClose) {
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (forceClose) {
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    assert channelFuture == future;
                    ctx.close();
                }
            });
        }
    }
}
