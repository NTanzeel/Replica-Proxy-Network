package old.rpn.server.net.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

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
