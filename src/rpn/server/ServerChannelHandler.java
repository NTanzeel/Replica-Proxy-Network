package rpn.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

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

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
