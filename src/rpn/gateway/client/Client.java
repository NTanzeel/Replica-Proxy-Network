package rpn.gateway.client;

import io.netty.channel.Channel;

public class Client {

    private int id;

    private String ip;

    private Channel channel;

    public Client(int id, String ip, Channel channel) {
        this.id = id;
        this.ip = ip;
        this.channel = channel;
    }

}
