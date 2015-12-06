package rpn.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import rpn.gateway.net.decoders.ConnectionDecoder;
import rpn.gateway.net.handlers.GatewayChannelHandler;

import java.util.logging.Logger;

/**
 * The main class wrapping the functionality of the gateway.
 */
public class Gateway {

    /**
     * A <code>Logger</code> used for printing debugging information of various type.
     */
    public static final Logger LOGGER = Logger.getLogger(Gateway.class.getName());

    /**
     * The port the gateway is bound to.
     */
    private final int port;

    /**
     * Creates a new instance to represent a functioning gateway.
     *
     * @param port The port the gateway needs to bind to.
     */
    public Gateway(int port) {
        this.port = port;
    }

    /**
     * Starts up the Netty server and runs a loop to monitor the connection.
     *
     * @throws Exception Thrown if there is an issue binding to the port.
     */
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

            ChannelFuture f = bootstrap.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * Entry point for the program.
     * A new instance of the <code>Gateway</code> is created using the port specified in the arguments.
     *
     * @param args The arguments for the program.
     * @throws Exception Any exceptions not caught are thrown to console.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please provide the gateway port.");
            return;
        }

        int port = Integer.parseInt(args[0]);

        new Gateway(port).run();
    }
}