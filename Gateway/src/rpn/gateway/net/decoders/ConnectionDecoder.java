package rpn.gateway.net.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpn.gateway.model.connection.Connection;
import rpn.gateway.model.connection.ConnectionHandler;

import java.util.List;

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
     * @param in A buffer containing an in-stream of bytes
     * @param out A decoded list of objects from the buffer.
     * @throws Exception Throws an Exception in case of any errors.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(in.readableBytes());
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
        if (in.readableBytes() > 4) {
            isServer = in.readInt() == 1;
            state = ConnectionState.Pending;
        }
    }

    /**
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in A buffer containing an in-stream of bytes
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
     * @param in A buffer containing an in-stream of bytes
     * @return A Connection object representing the servers connection or null
     */
    public Connection getServer(ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < 20)
            return null;

        String host = Integer.toString(in.readInt());
        for (int i = 0; i < 3; i++)
            host +=  "." + Integer.toString(in.readInt());

        int port = in.readInt();

        Connection server = ConnectionHandler.getInstance().register(ctx.channel(), host, port);

        writeResponse(ctx, 1, ((Boolean) server.getAttribute("isPrimary")) ? 1 : 0, false);

        return server;
    }

    /**
     * Gets a Connection object representing the clients connection, with various attributes attached, or null if
     * not enough information is available to produce a Connection object.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param in A buffer containing an in-stream of bytes
     * @return A Connection object representing the clients connection or null
     */
    public Connection getClient(ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < 16)
            return null;

        String host = Integer.toString(in.readInt());
        for (int i = 0; i < 3; i++)
            host +=  "." + Integer.toString(in.readInt());

        Connection client = null;

        try {
            client = ConnectionHandler.getInstance().register(ctx.channel(), host);
            writeResponse(ctx, 1, 1, false);
        } catch (IndexOutOfBoundsException e) {
            writeResponse(ctx, 0, 1, true);
        }

        return client;
    }

    /**
     * Writes a success or failure code back to the client/server and, if requested, closes the connection upon
     * completion.
     *
     * @param ctx The channel handler, passed through as a parameter by Netty
     * @param success Whether a connection has successfully been established
     * @param status The status of the connection, usually a 1 for success or an integer error code for failure
     * @param forceClose Whether the connection should be forcefully closed upon write completion
     */
    private void writeResponse(ChannelHandlerContext ctx, int success, int status, boolean forceClose) {
        ByteBuf out = ctx.alloc().buffer().writeInt(success).writeInt(status);
        ChannelFuture channelFuture = ctx.writeAndFlush(out);
        if (forceClose) {
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    assert channelFuture == future;
                    ctx.close();
                }
            });
        }
    }

    //    public void establishConnection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    //        Object connection;
    //
    //        if (isServer) {
    //            connection = getServer(ctx, in);
    //        } else {
    //            connection = getClient(ctx, in);
    //        }
    //
    //        if (connection != null) {
    //            out.add(connection);
    //            state = ConnectionState.Established;
    //            ctx.channel().pipeline().replace("decoder", "decoder", isServer ? new PacketDecoder() : new ClientDecoder());
    //        }
    //    }

    //    private Server getServer(ChannelHandlerContext ctx, ByteBuf in) {
    //        if (in.readableBytes() < 20)
    //            return null;
    //
    //        String host = Integer.toString(in.readInt());
    //        for (int i = 0; i < 3; i++)
    //            host +=  "." + Integer.toString(in.readInt());
    //
    //        int port = in.readInt();
    //
    //        Server server = ServerHandler.getInstance().register(ctx.channel(), host, port);
    //
    //        writeResponse(ctx, 1, server.isPrimary() ? 1 : 0, false);
    //
    //        return server;
    //    }

    //    private Client getClient(ChannelHandlerContext ctx, ByteBuf in) {
    //        if (in.readableBytes() < 16)
    //            return null;
    //
    //        String host = Integer.toString(in.readInt());
    //        for (int i = 0; i < 3; i++)
    //            host +=  "." + Integer.toString(in.readInt());
    //
    //        Client client = ClientHandler.getInstance().registerClient(ctx.channel(), host);
    //
    //        if (client == null)
    //            writeResponse(ctx, 0, 1, true);
    //        else
    //            writeResponse(ctx, 1, 0, false);
    //
    //        return client;
    //    }

}
