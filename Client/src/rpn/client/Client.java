package rpn.client;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;
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

            socket = new Socket(ip, port);

            out = new PrintWriter(socket.getOutputStream(), true);

            in = new DataInputStream(socket.getInputStream());

            /**
             * 0/1 to indicate boolean
             * 0 is client
             * 1 is server
             */
            out.write(0);

            int response = in.readInt();

            System.out.println("Reached.");

            if (response == -1) {
                System.out.println("Unable to connect to gateway server, limit reached.");
                socket.close();
            } else {
                System.out.println("Connected to gateway server with ID: " + response);
            }

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
        Client client = new Client("", 0);
        client.initialise();

    }

}