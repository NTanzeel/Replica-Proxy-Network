package rpn.server.model.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpn.server.model.gateway.GatewayHandler;

public abstract class Client implements Runnable {

    private String host;
    private int port;

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

    public void connect() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(getChannelInitializer());

            ChannelFuture f = b.connect(this.host, this.port).sync();

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
            System.out.println(e.getMessage());
        }
    }
}
