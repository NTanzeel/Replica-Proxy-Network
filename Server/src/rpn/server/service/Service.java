package rpn.server.service;

import java.util.HashMap;
import java.net.Socket;

public class Service {
    private HashMap<String, Stock> stocks = new HashMap<String, Stock>();
    private Socket socket;

    public Service(){
        initialiseMarket();
    }

    //Populate market with initial stock prices and quantities
    public void initialiseMarket(){
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
        for(HashMap.Entry<String, Stock> entry : stocks.entrySet()){
            stockString += entry.getKey() + "," + entry.getValue().getQuantity() + "," + entry.getValue().getPrice() + ";";
        }

        return stockString;
    }

    //Process the request received from the client
    public int processRequest(int code, String stock, int quantity){
        int status;

        //Depending on request code (buy or sell), update stock amount
        switch(code){
            case 0 :
                status = buyStock(stock, quantity);
                break;
            case 1:
                status = sellStock(stock, quantity);
                break;
            default:
                System.out.println("Invalid option");
                //Error code 3 represents an invalid option
                status = 3;
                break;
        }

        return status;
    }

    public boolean checkQuantity(int buyAmount, int availableStocks){
        if( buyAmount > availableStocks) { return false; }
        else { return true; }
    }

    public int buyStock(String stock, int quantity){
        int availableStocks = stocks.get(stock).getQuantity();

        if(checkQuantity(quantity, availableStocks)) {
            stocks.get(stock).setQuantity(availableStocks - quantity);

            return 1;
        }else{
            //Error code 2 represents trying to buy too many stocks
            return 2;
        }
    }

    public int sellStock(String stock, int quantity){
        stocks.get(stock).setQuantity(stocks.get(stock).getQuantity() - quantity);

        return 1;
    }
}
