package rpn.gateway.model.connection;

import io.netty.channel.Channel;

import java.util.HashMap;

public class Connection {

    private Channel channel;

    private HashMap<String, Object> attributes = new HashMap<>();

    public Connection(Channel channel) {
        this.channel = channel;
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Channel getChannel() {
        return channel;
    }

}
