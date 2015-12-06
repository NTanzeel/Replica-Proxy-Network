package rpn.gateway.model.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Responsible for handling all connections to the gateway. The ConnectionHandler is also responsible for handling
 * crashed, delegating a primary server and broadcasting any changes.
 */
public class ConnectionHandler {

    /**
     * Creates a single instance of the <code>ConnectionHandler</code> to be used globally.
     * The instance is limited to 10 client connections but this may be increased to any number.
     */
    public static final ConnectionHandler instance = new ConnectionHandler(10);

    /**
     * Gets a single instance of the <code>ConnectionHandler</code>.
     *
     * @return The instance of <code>ConnectionHandler</code>.
     */
    public static ConnectionHandler getInstance() {
        return instance;
    }

    /**
     * The limit of clients that may connect to the gateway.
     */
    private int limit;

    /**
     * The number of clients currently connected to the gateway.
     */
    private int noOfClients = 0;

    /**
     * An array of clients represented by the <code>Connection</code> class.
     */
    private final Connection[] clients;

    /**
     * A list of servers represented by the <code>Connection</code> class.
     */
    private final ArrayList<Connection> servers = new ArrayList<>();

    /**
     * Creates a new instance of the <code>ConnectionHandler</code> with a specific client limit.
     *
     * @param limit The maximum number of clients that may be connected at a single point in time.
     */
    public ConnectionHandler(int limit) {
        this.limit = limit;
        this.clients = new Connection[limit];
    }

    /**
     * Gets the maximum number of clients that may be connected at a single point in time
     *
     * @return The maximum number of clients that may be connected at a single point in time
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the number of clients currently connected to the gateway.
     *
     * @return The number of clients currently connected to the gateway.
     */
    public int getNoOfClients() {
        return noOfClients;
    }

    /**
     * The number of servers currently connected to the gateway.
     * This is represented by the size of the servers list.
     *
     * @return The number of servers currently connected to the gateway.
     */
    public int getNoOfServers() {
        return servers.size();
    }


    /**
     * Gets an available slot number. An index in the clients array. Calls to this method are synchronized.
     *
     * @return The next available free slot or -1 if there are no free slots.
     */
    private synchronized int getAvailableSlot() {
        int slot = -1;

        for (int i = 0; i < limit; i++) {
            if (clients[i] == null) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    /**
     * Checks to see if a client with the specifies id is registered.
     *
     * @param id The id of the client.
     * @return Boolean true or false, whether a client with the specifies id is registered.
     */
    public boolean clientExists(int id) {
        boolean exists;

        synchronized (clients) {
            exists = id >= 0 && id < clients.length && clients[id] != null;
        }

        return exists;
    }

    /**
     * Gets the <code>Connection</code> representing the client with the specified id.
     * Access to the clients array is synchronized so a call to this method may block until the lock is available.
     * An <code>IndexOutOfBoundsException</code> may be thrown if the id is out of range.
     *
     * @param id The id of the client.
     * @return The <code>Connection</code> representing the client with the specified id.
     * @throws IndexOutOfBoundsException Thrown if the id is out of range.
     */
    public Connection getClient(int id) throws IndexOutOfBoundsException {
        if (id < 0 || id >= clients.length) {
            throw new IndexOutOfBoundsException();
        }

        Connection client;

        synchronized (clients) {
            client = clients[id];
        }

        return client;
    }

    /**
     * Gets the <code>Connection</code> representing the server with the specified id.
     * Access to the servers list is synchronized so a call to this method may block until the lock is available.
     * An <code>IndexOutOfBoundsException</code> may be thrown if the id is out of range.
     *
     * @param id The id of the client.
     * @return The <code>Connection</code> representing the server with the specified id.
     * @throws IndexOutOfBoundsException Thrown if the id is out of range.
     */
    public Connection getServer(int id) throws IndexOutOfBoundsException {
        Connection server;

        synchronized (servers) {
            server = servers.get(id);
        }

        return server;
    }

    /**
     * Registers a new connection to the gateway.
     * This method in turn calls the next register method with port -1 to indicate a client.
     *
     * @param channel The channel associated with the connection.
     * @param host    The host ip address of the connection.
     * @return A <code>Connection</code> object wrapping the channel, host and port.
     * @throws IndexOutOfBoundsException Thrown if there are no slots available.
     */
    public Connection register(Channel channel, String host) throws IndexOutOfBoundsException {
        return register(channel, host, -1);
    }

    /**
     * Register a new connection to the gateway.
     * Determine using the port whether the connection is a client or a server and register it.
     *
     * @param channel The channel associated with the connection.
     * @param host    The host ip address of the the connection.
     * @param port    The port of the server, or -1 if the connection is a client.
     * @return A <code>Connection</code> object wrapping the channel, host and port.
     * @throws IndexOutOfBoundsException Thrown if there are no slots available.
     */
    public Connection register(Channel channel, String host, int port) throws IndexOutOfBoundsException {
        if (port < 0) {
            return registerClient(channel, host);
        } else {
            return registerServer(channel, host, port);
        }
    }

    /**
     * Register a new client connection to the gateway. Calls to this method are synchronized.
     * Access to the clients array is synchronized so a call to this method may block until the lock is available.
     *
     * @param channel The channel associated with the connection.
     * @param host    The host ip address of the the connection.
     * @return A <code>Connection</code> object, with additional attributes, representing the client.
     * @throws IndexOutOfBoundsException Thrown if there are no slots available.
     */
    private synchronized Connection registerClient(Channel channel, String host) throws IndexOutOfBoundsException {
        if (getNoOfClients() == getLimit()) {
            throw new IndexOutOfBoundsException();
        }

        int id = getAvailableSlot();

        Connection client = new Connection(channel);

        client.setAttribute("id", id);
        client.setAttribute("host", host);
        client.setAttribute("type", "CLIENT");

        synchronized (clients) {
            clients[id] = client;
            noOfClients++;
        }

        return client;
    }

    /**
     * Register a new server connection to the gateway. Calls to this method are synchronized.
     * Access to the servers list is synchronized so a call to this method may block until the lock is available.
     *
     * @param channel The channel associated with the connection.
     * @param host    The host ip address of the the connection.
     * @param port    The port the server is running on.
     * @return A <code>Connection</code> object, with additional attributes, representing the server.
     */
    private synchronized Connection registerServer(Channel channel, String host, int port) {
        Connection server = new Connection(channel);

        server.setAttribute("host", host);
        server.setAttribute("port", port);
        server.setAttribute("type", "SERVER");
        server.setAttribute("isPrimary", getNoOfServers() == 0);

        synchronized (servers) {
            servers.add(server);
        }

        return server;
    }

    /**
     * DeRegisters a connection from the gateway.
     * This method is called whenever a client or server disconnects from the gateway.
     *
     * @param connection The connection to remove.
     * @throws IllegalStateException Thrown if the last server is removed from the list.
     */
    public void deregister(Connection connection) throws IllegalStateException {
        if (connection.getAttribute("type").equals("CLIENT")) {
            deregisterClient(connection);
        } else {
            deregisterServer(connection);
        }
    }

    /**
     * DeRegister a client from the gateway. Calls to this method are synchronized.
     * Access to the clients array is synchronized so a call to this method may block until the lock is available.
     *
     * @param connection The connection to remove.
     * @throws IllegalStateException Thrown if an error occurs whilst attempting to remove the client.
     */
    private synchronized void deregisterClient(Connection connection) throws IllegalStateException {
        int id = (Integer) connection.getAttribute("id");

        connection.destruct();
        synchronized (clients) {
            clients[id] = null;
            noOfClients--;
        }
    }

    /**
     * DeRegister a server from the gateway. Calls to this method are synchronized.
     * Access to the servers list is synchronized so a call to this method may block until the lock is available.
     * Additionally, if the primary server is removed then a call to elect new primary is made.
     *
     * @param connection The connection to remove.
     * @throws IllegalStateException Thrown if the last server is removed from the list.
     */
    private synchronized void deregisterServer(Connection connection) throws IllegalStateException {
        connection.destruct();
        synchronized (servers) {
            servers.remove(connection);
        }
        if (connection.getAttribute("isPrimary").equals(Boolean.TRUE)) {
            electPrimary();
        }
    }

    /**
     * Elects a new primary server in the case that the primary goes offline.
     * After a new primary has been elected, it is notified of its new role and the details of the new primary are
     * broadcast to all the other replicas. Access to the servers list is required, which may cause this method to
     * block until the lock is available.
     *
     * @throws IllegalStateException Thrown if there are no available server to delegate a primary.
     */
    private void electPrimary() throws IllegalStateException {
        if (servers.size() == 0) {
            throw new IllegalStateException("No servers are currently online to takeover as primary.");
        }

        Connection primary;

        synchronized (servers) {
            primary = servers.get(0);
        }

        broadcastPrimary(primary);
    }

    /**
     * Broadcasts the details of the new primary to itself and all other replicas.
     * Access to the servers list is synchronized so a call to this method may block until the lock is available.
     *
     * @param primary The new primary to broadcast.
     */
    private void broadcastPrimary(Connection primary) {
        primary.setAttribute("isPrimary", true);

        String host = (String) primary.getAttribute("host");
        int port = (int) primary.getAttribute("port");


        Collection<Connection> servers;
        synchronized (this.servers) {
            servers = Collections.unmodifiableCollection(this.servers);

        }

        for (Connection server : servers) {
            sendPrimaryToReplica(server, host, port);
        }
    }

    /**
     * Sends a "Primary Changed" Packet to a specified replica.
     * If the specified replica is the new primary then only a role change packet is sent.
     *
     * @param replica The replica to send the packet to.
     * @param host    The host to write as part of the packet.
     * @param port    The port to write as part of the packet.
     */
    private void sendPrimaryToReplica(Connection replica, String host, int port) {
        ByteBuf out = replica.getChannel().alloc().buffer();

        boolean isPrimary = (boolean) replica.getAttribute("isPrimary");

        out.writeInt(1);
        out.writeInt(isPrimary ? 1 : 0);

        if (!isPrimary) {
            for (String octet : host.split("\\.")) {
                out.writeInt(Integer.parseInt(octet));
            }

            out.writeInt(port);
        }

        replica.getChannel().writeAndFlush(out);
    }
}
