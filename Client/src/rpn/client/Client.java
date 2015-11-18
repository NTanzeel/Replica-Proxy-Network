package rpn.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private double balance;
    private HashMap<String,Integer> stocks = new HashMap<String,Integer>();
    private String ip;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private DataInputStream in;
    private Scanner reader = new Scanner(System.in);

    public Client(String ip, int port) {
        // ip and port of Gateway
        this.ip = ip;
        this.port = port;
        //connect();
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

<<<<<<< HEAD
            socket = new Socket(ip, port);
=======
            Socket socket = new Socket(ip, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
>>>>>>> 7f4d4c7943555b5bda94ac35dae2a78dbf5d29bc

            out = new PrintWriter(socket.getOutputStream(), true);

<<<<<<< HEAD
            in = new DataInputStream(socket.getInputStream());

            /**
             * 0/1 to indicate boolean
             * 0 is client
             * 1 is server
             */
            out.write(0);
=======
            out.writeInt(0);
>>>>>>> 7f4d4c7943555b5bda94ac35dae2a78dbf5d29bc

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            for (String s : ipAddress.split("\\.")) {
                out.writeInt(Integer.parseInt(s));
            }

            out.flush();

<<<<<<< HEAD
            if (response == -1) {
                System.out.println("Unable to connect to gateway server, limit reached.");
                socket.close();
            } else {
                System.out.println("Connected to gateway server with ID: " + response);
            }
=======
            int response = in.readInt();
            int statusCode = in.readInt();

            System.out.println("Response: " + response + ", Status: " + statusCode);
>>>>>>> 7f4d4c7943555b5bda94ac35dae2a78dbf5d29bc

            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void buyStock() {
        printBuyConsole();
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

    public void printBuyConsole() {
       // Get stocks from server
        // ask user to enter name and nominal
    }



    public static void main(String args[]) {
<<<<<<< HEAD
        Client client = new Client("", 0);
        client.initialise();

=======
        new Client("0.0.0.0", 43590).connect();
>>>>>>> 7f4d4c7943555b5bda94ac35dae2a78dbf5d29bc
    }

}