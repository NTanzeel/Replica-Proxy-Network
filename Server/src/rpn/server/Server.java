package rpn.server;

import rpn.server.entities.gateway.Gateway;
import rpn.server.entities.primary.Primary;
import rpn.server.entities.listener.Listener;
import rpn.server.service.Service;

import java.net.InetAddress;
import java.util.logging.Logger;

public class Server {

    /**
     * A <code>Logger</code> used for printing debugging information of various type.
     */
    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Creates a single instance of the <code>Server</code> to be used globally.
     */
    private static Server instance;

    /**
     * Gets a single instance of the <code>Server</code>.
     *
     * @return The instance of <code>Server</code>.
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * A connection to the primary server, null if this server is the primary.
     */
    private Primary primary = null;

    /**
     * Whether this server is the primary server.
     */
    private boolean isPrimary = true;

    /**
     * The listener server for backups to connect to. Used to propagate requests.
     */
    private Listener listener;

    /**
     * A connection to the gateway.
     */
    private Gateway gateway;

    /**
     * The core service for manipulating the market.
     */
    private Service service = new Service();

    /**
     * Created a new server instance, wraps all networking to formulate a single server.
     * @param gatewayHost The host ip of the gateway.
     * @param gatewayPort The port of the gateway.
     * @param serverPort The port this server will run on.
     * @throws Exception Throws an exception if there's any errors.
     */
    public Server(String gatewayHost, int gatewayPort, int serverPort) throws Exception {
        String serverHost = InetAddress.getLocalHost().getHostAddress();

        this.listener = new Listener(serverPort);
        this.gateway = new Gateway(gatewayHost, gatewayPort, serverHost, serverPort);
    }

    /**
     * Gets whether this server is the primary.
     * @return Boolean true or false, whether this server is the primary.
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether this server is the primary.
     * @param isPrimary Whether this server is the primary.
     */
    public void isPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * Gets the connection to the primary.
     * @return The connection to the primary.
     */
    public Primary getPrimary() {
        return primary;
    }

    /**
     * Sets the connection to the primary and starts the primary thread.
     * @param primary A connection the primary.
     */
    public void setPrimary(Primary primary) {
        this.primary = primary;
        if (this.primary != null) {
            this.primary.start();
        }

        this.isPrimary(this.primary == null);
    }

    /**
     * Gets the connection to the gateway.
     * @return The connection to the gateway.
     */
    public Gateway getGateway() {
        return gateway;
    }

    /**
     * Gets the listener server that all backups connect to.
     * @return The listener server.
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Gets the core service for manipulating the market.
     * @return The core service for manipulating the market.
     */
    public Service getService() {
        return service;
    }

    /**
     * Starts the listener server followed by the gateway in separate threads.
     * @return The current instance of this class.
     */
    public Server start() {
        this.listener.start();
        this.gateway.start();

        return this;
    }

    /**
     * Stops any threads by terminating open connections.
     */
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

    /**
     * Main entry point used to create an instance of the server.
     * @param args Arguments passed in by the user at runtime.
     */
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
