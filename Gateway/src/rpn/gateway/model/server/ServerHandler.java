package rpn.gateway.model.server;

import io.netty.channel.Channel;

import java.util.ArrayList;

public class ServerHandler {

    private static final ServerHandler instance = new ServerHandler();

    public static ServerHandler getInstance() {
        return instance;
    }

    public ArrayList<Server> servers = new ArrayList<>();

    public Server register(Channel channel, String host, int port) {
        Server server = new Server(channel, host, port, servers.size() == 0);

        servers.add(server);

        return server;
    }

    public boolean deregister(Server server) {
        return false;
    }


}
