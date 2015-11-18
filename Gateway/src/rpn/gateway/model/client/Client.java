package rpn.gateway.model.client;

import io.netty.channel.Channel;

public class Client {

    private int id;

    private Channel channel;

    private String host;

    public Client(int id, Channel channel, String hos) {
        this.id = id;
        this.channel = channel;
        this.host = hos;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getHost() {
        return host;
    }

}
