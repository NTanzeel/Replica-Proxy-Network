package rpn.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Client {

    private double balance;
    private HashMap<String,Integer> stocks = new HashMap<String,Integer>();
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance += balance;
    }

    public HashMap<String,Integer> getStocks() {
        return stocks;
    }

    public void setStocks(String stockName, int quantity) {
        int initialQuantity = stocks.get(stockName);
        stocks.put(stockName, initialQuantity+quantity);
    }

    public void connect() {
        try {

            Socket socket = new Socket(ip, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeInt(0);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            for (String s : ipAddress.split("\\.")) {
                out.writeInt(Integer.parseInt(s));
            }

            out.flush();

            int response = in.readInt();
            int statusCode = in.readInt();

            System.out.println("Response: " + response + ", Status: " + statusCode);

            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Client("0.0.0.0", 43590).connect();
    }

}