package old.rpn.server.service;

import java.util.HashMap;

public class Service {
    /**
     * HashMap of available stocks
     */
    private HashMap<String, Stock> stocks = new HashMap<String, Stock>();

    /**
     * Constructor will initialise the Service by populating the stock market
     */
    public Service(){
        initialiseMarket();
    }

    /**
     * Populate market with initial stock prices and quantities
     */
    public void initialiseMarket(){
        Stock google = new Stock(100, 7);
        Stock twitter = new Stock(70, 6);
        Stock apple = new Stock(40, 10);

        stocks.put("GOOG", google);
        stocks.put("TWTR", twitter);
        stocks.put("APPL", apple);
    }

    /**
     * Send current stock market status to the client as a byte stream
     * @return Returns the formatted string of available stocks
     */
    public String getStocks(){
        String stockString = "";

        //Add each map entry to a string formatted as key,quantity, price;...;key,quantity,price;
        for(HashMap.Entry<String, Stock> entry : stocks.entrySet()){
            stockString += entry.getKey() + "," + entry.getValue().getQuantity() + "," + entry.getValue().getPrice() + ";";
        }
        return stockString;
    }

    /**
     * Method to process the request received from the client
     *
     * @param code represents whether the client is buying (0) or selling (1) stock
     * @param stock represents the name of the stock being bought or sold
     * @param quantity represents the amount of stock being bought or sold
     * @return Returns response code representing a successfully processed request or an error
     */
    public int processRequest(int code, String stock, int quantity){
        int status;

        //Check if the requested stock
        if( stockExists(stock)) {
            //Depending on request code (buy or sell), update stock amount
            switch (code) {
                case 0:
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
        }
        else{
            //Error code 4 represents a non-existant stock
            status = 4;
        }
        return status;
    }

    /**
     * Method to check if the requested stock quantity is available to buy
     *
     * @param buyAmount
     * @param availableStocks
     * @return Returns true if there are enough stocks to buy, false otherwise
     */
    public boolean checkQuantity(int buyAmount, int availableStocks){
        if( buyAmount > availableStocks) { return false; }
        else { return true; }
    }

    /**
     * Method to check is the requested stock exists
     *
     * @param stock represents the name of the stock being bought or sold
     * @return Returns true if the specified stock exists, false otherwise
     */
    public boolean stockExists(String stock){
        if(stocks.containsKey(stock)){ return true; }
        else { return false; }
    }

    /**
     * Method to reflect the clients purchase (reduces quantity for specified stock)
     *
     * @param stock represents the name of the stock being bought or sold
     * @param quantity represents the amount of stock being bought or sold
     * @return Returns response code representing a successfully processed request or an error
     */
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

    /**
     * Method to reflect the clients sale (increases quantity for specified stock)
     *
     * @param stock represents the name of the stock being bought or sold
     * @param quantity represents the amount of stock being bought or sold
     * @return Returns response code representing a successfully processed request or an error
     */
    //
    public int sellStock(String stock, int quantity){
        stocks.get(stock).setQuantity(stocks.get(stock).getQuantity() - quantity);

        return 1;
    }
}
