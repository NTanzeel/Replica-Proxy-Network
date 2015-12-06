package rpn.server.entities.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpn.server.Server;

/**
 * An abstract class representing a socket connection to a server as a client.
 */
public abstract class Client implements Runnable {

    /**
     * Whether the client is running.
     */
    private boolean isRunning = false;

    /**
     * The server host.
     */
    private String host;

    /**
     * The server port
     */
    private int port;

    /**
     * The channel associated with the connection.
     */
    private Channel channel;

    /**
     * Creates a new instance to connect to a server using a socket client.
     *
     * @param host The server host.
     * @param port The server port.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the channel associated with the connection.
     *
     * @return The channel associated with the connection.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Start the connection and open a socket.
     *
     * @throws Exception Thrown if there is an issue opening the socket.
     */
    public void connect() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(getChannelInitializer());

            ChannelFuture f = b.connect(this.host, this.port).sync();

            channel = f.channel();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Gets the channel initializer.
     *
     * @return The channel initializer.
     */
    protected ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                initialisePipeline(ch.pipeline());
            }
        };
    }

    /**
     * Abstract method to be overwritten by children to build the pipeline.
     *
     * @param pipeline The pipeline to initialise.
     */
    protected abstract void initialisePipeline(ChannelPipeline pipeline);

    /**
     * Called when this instance is run in a thread. Calls the connect() method to open a connection.
     */
    public void run() {
        try {
            connect();
        } catch (Exception e) {
            isRunning = false;
            Server.LOGGER.severe("Gateway - Status: Disconnected, Reason: '" + e.getMessage() + "'");
            Server.getInstance().stop();
        }
    }

    /**
     * Gets whether the connection is open and client is running.
     *
     * @return Boolean true or false, whether the client is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Starts this instance in a new thread if it isn't already running.
     */
    public void start() {
        if (isRunning) {
            throw new IllegalStateException("The thread is already running. Please terminate the existing thread first.");
        }

        new Thread(this).start();
        isRunning = true;
    }

    /**
     * Closes the channel which then proceeds to terminate the thread.
     */
    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("The thread is not running. Please run the thread first.");
        }

        channel.close();
        isRunning = false;
    }
}
