package rpn.server.model.connection;

import io.netty.channel.Channel;

public class Replica {

    private Channel channel;

    public Replica(Channel channel) {
        this.channel = channel;
    }

    public boolean update() {
        return true;
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }
}
