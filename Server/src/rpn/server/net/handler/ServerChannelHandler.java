package rpn.server.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class ServerChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Connected");

        ByteBuf buff = ctx.alloc().buffer();

        String message = "Welcome, Cunt.\n";

        buff.writeBytes(message.getBytes());

        ctx.writeAndFlush(buff);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client Disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        ByteBuf out = ctx.alloc().buffer();

        try {
            String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.print(host + " Says: ");
            while (in.isReadable()) {
                byte b = in.readByte();
                System.out.print((char) b);
                out.writeByte(b);
            }
        } finally {
            ctx.writeAndFlush(out);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
