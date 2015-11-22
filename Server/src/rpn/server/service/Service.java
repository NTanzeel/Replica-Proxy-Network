package rpn.server.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Service {
    private HashMap<String, Stock> stocks = new HashMap<String, Stock>();
    private String ip;
    private int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Service(String ip, int port){
        //Specify IP address and port number of Gateway
        this.ip = ip;
        this.port = port;
        initaliseMarket();
    }

    //Populate market with initial stock prices and quantities
    public void initaliseMarket(){
        Stock google = new Stock(100, 7);
        Stock twitter = new Stock(70, 6);
        Stock apple = new Stock(40, 10);

        stocks.put("GOOG", google);
        stocks.put("TWTR", twitter);
        stocks.put("APPL", apple);
    }

    //Send current stock market status to the client as a byte stream
    public void sendStocks(){
        try{
            //convert map to stream of format name;quantity;price
            for(Map.Entry<String, Stock> entry : stocks.entrySet()){
                String output = entry.getKey() + "," + entry.getValue().getQuantity() + "," + entry.getValue().getPrice() + ";";
                out.writeBytes(output);
                out.flush();
            }
        }
        catch(Exception e){
            System.out.println("Error sending available stocks");
        }
    }

    public void processRequest(int request, String stock, int quantity){
        //Request to buy stock
        if(request == 0){

        }
        //Request to sell stock
        else{

        }
    }

    public static void main(String args[]) {
        Service client = new Service("192.168.0.23", 43590);
    }
}
