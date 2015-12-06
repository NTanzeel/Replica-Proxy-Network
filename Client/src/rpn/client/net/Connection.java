package rpn.client.net;

import rpn.client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

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

    public Connection(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(getHost(), getPort());

        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }


    /**
     * Throws exception if the client limit is reached or gateway refuses connection.
     *
     * @throws IOException
     */
    public void init() throws IOException {
        int response[] = handshake();

        if (response[0] == 0) {
            if (response[1] == 1) {
                throw new IOException("Unable to connect to server, client limit reached.");
            } else if (response[1] == 2) {
                throw new IOException("Unable to connect to server, connection refused.");
            }
        }
    }

    /**
     * Initalise connection with the server, tell that it is client,
     *
     * server replies with resposnse.
     *
     * @return integer array of the responses from the server.
     * @throws IOException
     */
    private int[] handshake() throws IOException {
        outputStream.writeInt(0);

        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        for (String s : ipAddress.split("\\.")) {
            outputStream.writeInt(Integer.parseInt(s));
        }

        outputStream.flush();

        return new int[]{inputStream.readInt(), inputStream.readInt()};
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public boolean close() {
        try {
            socket.close();
        } catch (IOException e) {
            Client.LOGGER.severe("Error closing the socket.");
        }

        return socket.isClosed();
    }
}
