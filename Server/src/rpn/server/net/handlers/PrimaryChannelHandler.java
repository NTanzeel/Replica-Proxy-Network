package rpn.server.net.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpn.server.Server;
import rpn.server.net.decoders.GatewayConnectionDecoder;

import java.nio.charset.Charset;

public class PrimaryChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Server.LOGGER.info("Primary - Status: Connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Server.LOGGER.info("Primary - Status: Disconnected, Response: 'Reverting to connection Decoder'");
        Server.getInstance().getGateway().getChannel().pipeline().replace("decoder", "decoder", new GatewayConnectionDecoder());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;

        int request = in.readInt();

        String stocks = in.toString(Charset.forName("UTF-8"));

        Server.getInstance().getService().update(stocks);

        ctx.writeAndFlush(ctx.alloc().buffer(4).writeInt(request));

        Server.LOGGER.info("Request (ID: " + request + ") - Status: Acknowledged");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Server.LOGGER.severe("Primary - Status: Disconnected, Reason: " + cause.getMessage());
        Server.getInstance().stop();
    }
}
