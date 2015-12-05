package rpn.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import rpn.gateway.net.decoders.ConnectionDecoder;
import rpn.gateway.net.handlers.GatewayChannelHandler;

import java.util.logging.Logger;

public class Gateway {

    public static final Logger LOGGER = Logger.getLogger(Gateway.class.getName());

    private final int port;

    private Channel channel;

    public Gateway(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("decoder", new ConnectionDecoder())
                                    .addLast("handler", new GatewayChannelHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bootstrap.bind(port).sync(); // (7)

            channel = f.channel();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stop() {
        channel.close();
    }

    public static void main(String[] args) throws Exception {
        int port = 43590;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Gateway(port).run();
    }
}