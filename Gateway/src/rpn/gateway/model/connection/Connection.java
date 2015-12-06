package rpn.gateway.model.connection;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * Represents a connection, client or server, to the gateway.
 */
public class Connection {

    /**
     * Whether the connection is active.
     */
    private boolean isActive = true;

    /**
     * The channel associated with the connection, used to communicate with the client.
     */
    private Channel channel;

    /**
     * Any user specified attributes attached to this connection.
     */
    private HashMap<String, Object> attributes = new HashMap<>();

    /**
     * Instantiate a new connection.
     * @param channel The channel associated with the connection.
     */
    public Connection(Channel channel) {
        this.channel = channel;
    }

    /**
     * Checks to see whether the client has an attribute assigned.
     * @param key The key/name for the attribute.
     * @return Boolean true or false, whether the attribute is assigned.
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Gets an attribute by key and returns the object associated with it.
     * @param key The key/name for the attribute.
     * @return The object associated with they key.
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Sets an attribute for this connection instance.
     * @param key The key/name for the attribute.
     * @param value The object to associate with the key.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Gets the channel associated with this connection.
     * @return The channel associated with this connection
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets whether the connection is active.
     * @return whether the connection is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Destroys the connection by closing the channel associated with it.
     */
    public void destruct() {
        if (channel.isOpen()) {
            channel.close();
        }
        this.isActive = false;
    }

}
