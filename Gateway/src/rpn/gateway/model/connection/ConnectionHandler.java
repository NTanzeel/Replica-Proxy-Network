package rpn.gateway.model.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ConnectionHandler {

    public static final ConnectionHandler instance = new ConnectionHandler(2);

    public static ConnectionHandler getInstance() {
        return instance;
    }

    private int limit;

    private int noOfClients = 0;

    private final Connection[] clients;

    private final ArrayList<Connection> servers = new ArrayList<>();

    public ConnectionHandler(int limit) {
        this.limit = limit;
        this.clients = new Connection[limit];
    }

    public int getLimit() {
        return limit;
    }

    public int getNoOfClients() {
        return noOfClients;
    }

    public int getNoOfServers() {
        return servers.size();
    }

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

    public boolean clientExists(int id) {
        boolean exists;

        synchronized (clients) {
            exists = id >= 0 && id < clients.length && clients[id] != null;
        }

        return exists;
    }

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

    public Connection getServer(int index) throws IndexOutOfBoundsException {
        Connection server;

        synchronized (servers) {
            server = servers.get(index);
        }

        return server;
    }

    public Connection register(Channel channel, String host) throws IndexOutOfBoundsException {
        return register(channel, host, -1);
    }

    public Connection register(Channel channel, String host, int port) throws IndexOutOfBoundsException {
        if (port < 0) {
            return registerClient(channel, host);
        } else {
            return registerServer(channel, host, port);
        }
    }

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

    public void deregister(Connection connection) throws IllegalStateException {
        if (connection.getAttribute("type").equals("CLIENT")) {
            deregisterClient(connection);
        } else {
            deregisterServer(connection);
        }
    }

    private synchronized void deregisterClient(Connection connection) throws IllegalStateException {
        int id = (Integer) connection.getAttribute("id");

        connection.destruct();
        synchronized (clients) {
            clients[id] = null;
            noOfClients--;
        }
    }

    private synchronized void deregisterServer(Connection connection) throws IllegalStateException {
        connection.destruct();
        synchronized (servers) {
            servers.remove(connection);
        }
        if (connection.getAttribute("isPrimary").equals(Boolean.TRUE)) {
            electPrimary();
        }
    }

    private void electPrimary() throws IllegalStateException {
        if (servers.size() == 0){
            throw new IllegalStateException("No servers are currently online to takeover");
        }

        Connection primary;

        synchronized (servers) {
            primary = servers.get(0);
        }

        broadcastPrimary(primary);
    }
    
    /**
     * broadcasts the new allocated primary to each available online back-up server.
     *
     * @param primary The, new, primary to broadcast.
     */
    private void broadcastPrimary(Connection primary) {
        primary.setAttribute("isPrimary", true);

        String host = (String) primary.getAttribute("host");
        int port = (int) primary.getAttribute("port");


        Collection<Connection> servers;
        synchronized (this.servers) {
            servers = Collections.unmodifiableCollection(this.servers);

        }

        for(Connection server : servers) {
            sendPrimaryToReplica(server, host, port);
        }
    }

    /**
     * Sends a "Primary Changed" Packet to a specified replica.
     *
     * @param replica The replica to send the packet to.
     * @param host The host to write as part of the packet.
     * @param port The port to write as part of the packet.
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
