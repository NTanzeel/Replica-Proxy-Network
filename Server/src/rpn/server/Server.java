package rpn.server;

import rpn.server.entities.gateway.Gateway;
import rpn.server.entities.primary.Primary;
import rpn.server.entities.listener.Listener;
import rpn.server.service.Service;

import java.net.InetAddress;
import java.util.logging.Logger;

public class Server {

    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static Server instance;

    public static Server getInstance() {
        return instance;
    }

    private Primary primary = null;

    private boolean isPrimary = true;

    private Listener listener;

    private Gateway gateway;

    private Service service = new Service();

    public Server(String gatewayHost, int gatewayPort, int serverPort) throws Exception {
        String serverHost = InetAddress.getLocalHost().getHostAddress();

        this.listener = new Listener(serverPort);
        this.gateway = new Gateway(gatewayHost, gatewayPort, serverHost, serverPort);
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void isPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Primary getPrimary() {
        return primary;
    }

    public void setPrimary(Primary primary) {
        this.primary = primary;
        if (this.primary != null) {
            this.primary.start();
        }

        this.isPrimary(this.primary == null);
    }

    public Gateway getGateway() {
        return gateway;
    }

    public Listener getListener() {
        return listener;
    }

    public Service getService() {
        return service;
    }

    public Server start() {
        this.listener.start();
        this.gateway.start();

        return this;
    }

    public void stop() {
        LOGGER.info("Server - Status: Shutting Down");
        if (!isPrimary && primary.isRunning()) {
            getPrimary().stop();
        }
        if (listener.isRunning()) {
            getListener().stop();
        }
        if (gateway.isRunning()) {
            getGateway().stop();
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide the gateway host, gateway port, and a server port.");
            return;
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
