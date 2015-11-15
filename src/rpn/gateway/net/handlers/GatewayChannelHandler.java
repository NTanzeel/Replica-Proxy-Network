package rpn.gateway.net.handlers;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.gateway.client.Client;
import rpn.gateway.client.ClientHandler;

public class GatewayChannelHandler extends ChannelHandlerAdapter {

    private Client client = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        client = ClientHandler.getInstance().registerClient(ctx.channel());
        if (client == null) {
            ChannelFuture response = ctx.writeAndFlush(ctx.alloc().buffer().writeInt(-1));
            response.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    assert response == future;
                    ctx.close();
                }
            });
        } else {
            ctx.writeAndFlush(ctx.alloc().buffer().writeInt(client.getId()));
            System.out.println("Client Connected: " + client.getId());
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (client != null) {
            ClientHandler.getInstance().deregisterClient(client);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
