package rpn.server.service;

import java.util.HashMap;

public class Service {

    /**
     * HashMap of available stocks
     */
    private HashMap<String, Stock> stocks = new HashMap<>();

    /**
     * Constructor will initialise the Service by populating the stock market
     */
    public Service() {
        this("GOOG,9000,7;TWTR,7000,6;APPL,4000,10;");
    }

    public Service(String stocks) {
        initialiseMarket(stocks);
    }

    /**
     * Populate market with initial stock prices and quantities
     */
    public void initialiseMarket(String stocks) {
        for (String s : stocks.split(";")) {
            String[] stock = s.split(",");
            this.stocks.put(stock[0], new Stock(stock[0], Integer.parseInt(stock[1]), Integer.parseInt(stock[2])));
        }
    }

    /**
     * Method to check if the requested stock quantity is available to buy
     *
     * @param buyAmount the amount to buy.
     * @param availableStocks the available amount.
     * @return Returns true if there are enough stocks to buy, false otherwise
     */
    public boolean checkQuantity(int buyAmount, int availableStocks) {
        return buyAmount <= availableStocks;
    }

    /**
     * Method to reflect the clients purchase (reduces quantity for specified stock)
     *
     * @param stock    represents the name of the stock being bought or sold
     * @param quantity represents the amount of stock being bought or sold
     * @return Returns response code representing a successfully processed request or an error
     */
    public int buyStock(String stock, int quantity) {
        int availableStocks = stocks.get(stock).getQuantity();

        if (checkQuantity(quantity, availableStocks)) {
            stocks.get(stock).setQuantity(availableStocks - quantity);

            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Method to reflect the clients sale (increases quantity for specified stock)
     *
     * @param stock    represents the name of the stock being bought or sold
     * @param quantity represents the amount of stock being bought or sold
     * @return Returns response code representing a successfully processed request or an error
     */
    //
    public int sellStock(String stock, int quantity) {
        stocks.get(stock).setQuantity(stocks.get(stock).getQuantity() - quantity);

        return 1;
    }


    public void update(String stocks) {
        this.stocks.clear();
        initialiseMarket(stocks);
    }

    public String toString() {
        String toString = "";

        for (Stock stock : stocks.values()) {
            toString += stock.getName() + "," + stock.getQuantity() + "," + stock.getPrice() + ";";
        }

        return toString;
    }
}
