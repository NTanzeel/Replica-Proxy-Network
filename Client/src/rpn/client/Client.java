package rpn.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private double balance;
    private HashMap<String, Stock> ownedStocks = new HashMap<String, Stock>();
    private HashMap<String, Stock> availableStocks = new HashMap<String, Stock>();
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

    public HashMap<String, Stock> getStocks() {
        return ownedStocks;
    }

    public void addStocks(String stockName, int quantity) {
        Stock stock = ownedStocks.get(stockName);
        stock.setQuantity(stock.getQuantity() + quantity);
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
            // closeConnection();
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
        boolean goodInput = false;

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
     * Send 0, stock name, and amount to
     */
    public void buyStock() {
        try {

            String stock = "";
            int amount = 0;

            boolean isValid = false;

            getStocksFromMarket();
            printBuyConsole();

            while (!isValid) {

                System.out.print("Please enter a stock name: ");
                stock = reader.nextLine();
                System.out.print("Please enter an amount: ");
                amount = reader.nextInt();
                isValid = isValid(stock, amount);

            }

            out.writeBytes("1," + stock + "," + amount + ";");
            out.flush();

            while (in.available() < 4) {}

            int success = in.readInt();

            while (in.available() < 4) {}

            int response = in.readInt();

            if(success == 1) {
                addStocks(stock, amount);
                setBalance(amount*availableStocks.get(stock).getPrice()*-1);
            } else {

                switch(response) {
                    case 2:
                        System.out.println("Not enough " + stock + " stocks available. Please resubmit.");
                        break;
                    case 3:
                        System.out.println("Invalid Option");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isValid(String stock, int amount) {

        if(!availableStocks.containsKey(stock)) {
            System.out.println("Invalid stock name, please resubmit");
            return false;
        } else if (availableStocks.get(stock).getQuantity() < amount || amount <= 0) {
            System.out.println("Invalid quantity, please resubmit");
            return false;
        } else if (amount * availableStocks.get(stock).getPrice() > balance) {
            System.out.println("You cannot afford" + amount + " " + stock + " stocks");
            System.out.println("The maximum number you can afford with £" + balance
                    + " is " + balance/availableStocks.get(stock).getPrice());
            return false;
        } else {
            return true;
        }

    }


    /**
     * Requests formatted data of available stocks
     * Covert these into a Hashmap of available stocks
     * Format: name,quantity,price;
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

            byte[] buf = new byte[length];
            in.read(buf);

            String[] decoded = (new String(buf, "UTF-8")).split(";");

            for(int i=0; i<decoded.length; i++) {

                String[] split = decoded[i].split(",");
                if(availableStocks.containsKey(split[0])) {
                    availableStocks.get(split[0]).setQuantity(Integer.parseInt(split[1]));
                    availableStocks.get(split[0]).setQuantity(Integer.parseInt(split[2]));
                } else {
                    Stock stock = new Stock(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    availableStocks.put(split[0],stock);
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
     */
    public void printBuyConsole() {
        System.out.println("|⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻ BUY STOCK ⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻|");
        System.out.println("|                                     |");
        System.out.println("|~~~~~~~~~~~ CHOOSE OPTION ~~~~~~~~~~~|");
        System.out.println("|                                     |");
        System.out.println("| Name         Quantity      Price(£) |");
        printAvailableStocks();
        System.out.println("|_____________________________________|");
        System.out.println("");
    }

    public void printAvailableStocks() {
        for (HashMap.Entry<String,Stock> entry : availableStocks.entrySet()) {
            System.out.println("  "
                    + entry.getKey()
                    + "            "
                    + entry.getValue() );
        }
    }



    public static void main(String args[]) {
        Client client = new Client("ntanzeel.noip.me", 43590);
        client.initialise();
    }

}