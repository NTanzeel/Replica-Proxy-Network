package rpn.server.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Scanner;

public class ManualChannelHandler extends ChannelHandlerAdapter {

    private Scanner s = new Scanner(System.in);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        ByteBuf out = ctx.alloc().buffer();


        try {
            int opCode = in.readInt();

            switch (opCode) {
                case 0:
                    System.out.println("User has requested to buy stocks, Please respond:");
                    out.writeInt(4);
                    out.writeInt(s.nextInt());
                    break;

                case 1:
                    System.out.println("User has requested to sell stocks, Please respond:");
                    out.writeInt(4);
                    out.writeInt(s.nextInt());
                    break;

                case 2:
                    System.out.println("User has requested a list of all stocks, Please respond:");
                    byte output[] = s.nextLine().getBytes();
                    out.writeInt(output.length);
                    out.writeBytes(output);
                    break;
            }
        } finally {
            in.release();
            ctx.writeAndFlush(out);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
