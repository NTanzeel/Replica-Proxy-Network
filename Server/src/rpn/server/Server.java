package rpn.server;

import rpn.server.model.gateway.Gateway;
import rpn.server.model.primary.Primary;
import rpn.server.replicaserver.Listener;

import java.net.InetAddress;

public class Server {

    private static Server instance;

    public static Server getInstance() {
        return instance;
    }

    private Primary primary = null;

    private Listener listener;

    private Gateway gateway;

    public Server(String gatewayHost, int gatewayPort, int serverPort) throws Exception {
        String serverHost = InetAddress.getLocalHost().getHostAddress();

        this.listener = new Listener(gatewayPort);
        this.gateway = new Gateway(gatewayHost, gatewayPort, serverHost, serverPort);
    }

    public boolean isPrimary() {
        return primary == null;
    }

    public Primary getPrimary() {
        return primary;
    }

    public void setPrimary(Primary primary) {
        this.primary = primary;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public Listener getListener() {
        return listener;
    }

    public void stop() {

    }

    public Server start() {
        new Thread(gateway).start();

        return this;
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide the gateway host, gateway port, and a server port.");
        }

        String gatewayHost = args[0];
        int gatewayPort = Integer.parseInt(args[1]);
        int serverPort = Integer.parseInt(args[2]);

        try {
            instance = new Server(gatewayHost, gatewayPort, serverPort).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
