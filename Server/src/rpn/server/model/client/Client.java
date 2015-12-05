package rpn.server.model.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpn.server.Server;

public abstract class Client implements Runnable {

    private boolean isRunning = false;

    private String host;
    private int port;

    private Channel channel;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Channel getChannel() {
        return channel;
    }

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

    protected ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                initialisePipeline(ch.pipeline());
            }
        };
    }

    protected abstract void initialisePipeline(ChannelPipeline pipeline);

    public void run() {
        try {
            connect();
        } catch (Exception e) {
            isRunning = false;
            Server.LOGGER.severe("Gateway - Status: Disconnected, Reason: '" + e.getMessage() + "'");
            Server.getInstance().stop();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("The thread is already running. Please terminate the existing thread first.");
        }

        new Thread(this).start();
        isRunning = true;
    }

    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("The thread is not running. Please run the thread first.");
        }

        channel.close();
        isRunning = false;
    }
}
