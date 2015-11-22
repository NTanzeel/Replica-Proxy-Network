package rpn.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {

    /**
     * The server host
     */
    private String host;

    /**
     * The server port
     */
    private int port;

    /**
     * A socket to the server
     */
    private Socket socket;

    /**
     * The incoming stream of data from the server to the client
     */
    private DataInputStream inputStream = null;

    /**
     * The outgoing stream of data from the client to the server
     */
    private DataOutputStream outputStream = null;

    public Connection(String host, int port) throws UnknownHostException, IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);

        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void init() throws IOException {
        int response[] = handshake();

        if (response[0] == 0) {
            throw new IOException("Unable to connect to server, client limit reached.");
        }
    }

    private int[] handshake() throws IOException {
        outputStream.writeInt(0);

        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        for (String s : ipAddress.split("\\.")) {
            outputStream.writeInt(Integer.parseInt(s));
        }

        outputStream.flush();

        return new int[] {inputStream.readInt(), inputStream.readInt()};
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isActive() {
        return socket.isConnected();
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public boolean close() throws IOException {
        if (socket.isConnected()) {
            socket.close();
        }

        return socket.isClosed();
    }
}
