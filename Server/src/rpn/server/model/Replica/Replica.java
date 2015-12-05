package rpn.server.model.replica;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class Replica {

    private int id;

    private Channel channel;

    public Replica(Channel channel) {
        this.id = channel.hashCode();
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void update(String stocks) {
        update(1, stocks);
    }

    public void update(int request, String stocks) {
        ByteBuf out = channel.alloc().buffer(8 + stocks.length());
        out.writeInt(stocks.length() + 4);
        out.writeInt(request);
        out.writeBytes(stocks.getBytes());

        channel.writeAndFlush(out);
    }
}
