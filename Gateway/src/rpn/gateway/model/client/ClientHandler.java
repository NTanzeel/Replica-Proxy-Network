package rpn.gateway.model.client;

import io.netty.channel.Channel;

public class ClientHandler {

    private static ClientHandler instance = new ClientHandler();

    public static ClientHandler getInstance() {
        return instance;
    }

    private Client[] clients = new Client[10];

    public boolean exists(int id) {
        return clients[id] != null;
    }

    public Client get(int id) {
        return clients[id];
    }

    private int getId() {
        int id = -1;

        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                id = i;
                break;
            }
        }

        return id;
    }

    public Client registerClient(Channel channel, String host) {
        int id = getId();

        if (id == -1)
            return null;

        clients[id] = new Client(id, channel, host);

        return clients[id];
    }

    public boolean deregisterClient(Client client) {
        clients[client.getId()] = null;

        return clients[client.getId()] == null;
    }

}
