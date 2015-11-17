package rpn.gateway.model.server;

import io.netty.channel.Channel;

public class Server {

    private Channel channel;

    private String host;
    private int port;

    private boolean isPrimary;

    public Server(Channel channel, String host, int port, boolean isPrimary) {
        this.channel = channel;
        this.host = host;
        this.port = port;
        this.isPrimary = isPrimary;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void promote() {
        this.isPrimary = true;
    }
}
