package rpn.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private double balance;
    private HashMap<String,Integer> ownedStocks = new HashMap<String,Integer>();
    private HashMap<String,Integer> availableStocks = new HashMap<String,Integer>();
    private String ip;
    private int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner reader = new Scanner(System.in);

    public Client(String ip, int port) {
        // ip and port of Gateway
        this.ip = ip;
        this.port = port;
        connect();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance += balance;
    }

    public HashMap<String,Integer> getStocks() {
        return ownedStocks;
    }

    public void setStocks(String stockName, int quantity) {
        int initialQuantity = ownedStocks.get(stockName);
        ownedStocks.put(stockName, initialQuantity+quantity);
    }

    public void connect() {
        try {
            socket = new Socket(ip, port);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            /**
             * 0/1 to indicate boolean
             * 0 is client
             * 1 is server
             */
            out.writeInt(0);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            for (String s : ipAddress.split("\\.")) {
                out.writeInt(Integer.parseInt(s));
            }

            out.flush();

            int response = in.readInt();
            int statusCode = in.readInt();

            if (response == 0) {
                handleError(statusCode);
            } else {
                System.out.println("Response code: " + response);
                System.out.println("Status code: " + statusCode);
                System.out.println("Successfully connected to server.");
            }

            while(true){

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void handleError(int errorCode) {
        switch (errorCode) {
            case 1:
                System.out.println("Unable to connect to server, client limit reached.");
                break;
        }
    }

    private boolean closeConnection() {
        if (socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Error closing connection");
            }
        }

        return socket.isClosed();
    }

    public void initialise() {

        printConsole();
        Boolean goodInput = false;

        while(!goodInput) {

            char op = reader.nextLine().charAt(0);

            goodInput = true;

            switch (op) {
                case 'a':
                    buyStock();
                    break;
                case 'b':
                    sellStock();
                    break;
                case 'c':
                    getBalance();
                    break;
                case 'q':
                    break;
                default:
                    goodInput = !goodInput;
                    System.out.println("Invalid Option");
                    break;
            }
        }

    }

    /**
     * Request and output available stocks
     * Ask to enter valid name (a valid key in HashMap)
     * Ask to enter valid, afforable amount (stored in tuple value in HashMap
     * Send 1, stock name, and amount to
     */
    public void buyStock() {
        getStocksFromMarket();
        printBuyConsole();
    }


    /**
     * Requests formatted data of available stocks
     * Covert these into a Hashmap of available stocks
     */

    public void getStocksFromMarket() {

        int length = 0;

        try {
            out.writeInt(-1);
            out.flush();

            while (in.available() < 4) {

            }

            if (in.available() == 4) {
                length = in.readInt();
            }

            while (in.available() < length) {

            }

            if (in.available() == length) {

                byte[] buf = new byte[length];
                in.read(buf);

                String[] decoded = (new String(buf, "UTF-8")).split(";");

                for(int i=0; i<decoded.length; i++) {
                    String[] split = decoded[i].split(",");
                    availableStocks.put(split[0],Integer.parseInt(split[1]));
                }

            }

        } catch (Exception e) {
            System.out.println("Error requesting available stocks");
        }
    }

    public void sellStock() {

    }

    public void printConsole() {

        System.out.println("|⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻ STOCK  MARKET ⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻|");
        System.out.println("|                                     |");
        System.out.println("|~~~~~~~~~~~ CHOOSE OPTION ~~~~~~~~~~~|");
        System.out.println("|                                     |");
        System.out.println("|  a) Buy Stock                       |");
        System.out.println("|  b) Sell Stock                      |");
        System.out.println("|  c) Check Balance                   |");
        System.out.println("|  q) Quit                            |");
        System.out.println("|_____________________________________|");
        System.out.println("");
        System.out.print("Option: ");

    }

    /**
     * Show what stocks available (given by getStocksFromMarket())
     *
      */
    public void printBuyConsole() {
        System.out.println("|⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻ BUY STOCK ⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻|");
        System.out.println("|                                     |");
        System.out.println("|~~~~~~~~~~~ CHOOSE OPTION ~~~~~~~~~~~|");
        System.out.println("|                                     |");
        System.out.println("|        Name             Quantity    |");
        printAvailableStocks();
        System.out.println("|_____________________________________|");
        System.out.println("");
    }

    public void printAvailableStocks() {
        for (HashMap.Entry<String,Integer> entry : availableStocks.entrySet()) {
            System.out.println("         "
                    + entry.getKey()
                    + "             "
                    + entry.getValue() );
        }
    }



    public static void main(String args[]) {
        Client client = new Client("192.168.0.23", 43590);
        client.initialise();
    }

}