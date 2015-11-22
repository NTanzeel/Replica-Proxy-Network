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

    public Service(){
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
    public String sendStocks(){
        String stockString = "";

        //Convert map to stream of format name;quantity;price
        for(Map.Entry<String, Stock> entry : stocks.entrySet()){
            stockString += entry.getKey() + "," + entry.getValue().getQuantity() + "," + entry.getValue().getPrice() + ";";
        }

        return stockString;
    }

    public void processRequest(int code, String stock, int quantity){
        int currentQuantity = stocks.get(stock).getQuantity();

        //Request to buy stock
        if(code == 0){
            stocks.get(stock).setQuantity(currentQuantity - quantity);
        }
        //Request to sell stock
        else{
            stocks.get(stock).setQuantity(currentQuantity + quantity);
        }
    }
}
