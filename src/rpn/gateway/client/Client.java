package rpn.gateway.client;

import io.netty.channel.Channel;

public class Client {

    private int id;

    private Channel channel;

    public Client(int id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

}
