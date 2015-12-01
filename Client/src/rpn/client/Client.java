package rpn.client;

import rpn.client.io.Menu;
import rpn.client.model.Command;
import rpn.client.model.Stock;
import rpn.client.net.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private Connection connection;

    private Menu menu = new Menu(Command.list(), Command.QUIT);

    /**
     * The users balance - Represents the amount of a currency available
     */
    private double balance;

    private HashMap<String, Stock> ownedStocks = new HashMap<>();

    public Client(String host, int port) throws IOException {
        this.connection = new Connection(host, port);
    }

    private void updateBalance(double balance) {
        this.balance += balance;
    }

    private void updateStock(Stock stock) {
        if (ownedStocks.containsKey(stock.getName())) {
            stock.setQuantity(stock.getQuantity() + ownedStocks.get(stock.getName()).getQuantity());
        }

        ownedStocks.put(stock.getName(), stock);
    }

    public void run() throws IOException {
        this.connection.init();

        this.runDisplayLoop();

        this.connection.close();
    }

    private void runDisplayLoop() {
        Command choice;
        while (!menu.getTerminator().equals(choice = menu.getMainChoice())) {
            handleUserChoice(choice);
        }
    }

    private void handleUserChoice(Command command) {
        switch (command) {
            case BUY:
                buyStock(menu.getStockName(), menu.getQuantity());
                break;
            case SELL:
                sellStock(menu.getStockName(), menu.getQuantity());
                break;
            case REFRESH:
                viewStockList();
                break;
            case BALANCE:
                printBalance();
                break;
            default:
                System.out.println("Invalid Option");
                break;
        }
    }

    /**
     * Requests formatted data of available stocks
     * Covert these into a Hashmap of available stocks
     * Format: name,quantity,price;
     */

    public HashMap<String, Stock> getStocksFromMarket() {
        HashMap<String, Stock> stocks = new HashMap<>();

        try {
            connection.getOutputStream().writeInt(Command.REFRESH.getValue());
            connection.getOutputStream().flush();

            byte[] buffer = new byte[connection.getInputStream().readInt()];
            connection.getInputStream().readFully(buffer);

            String[] stockArray = (new String(buffer, "UTF-8")).split(";");

            for (String stockDetails : stockArray) {
                String stockSplit[] = stockDetails.split(",");
                Stock stock = new Stock(stockSplit[0], Integer.parseInt(stockSplit[1]), Integer.parseInt(stockSplit[2]));
                stocks.put(stockSplit[0], stock);
            }
        } catch (Exception e) {
            System.out.println("Error requesting available stocks");
            LOGGER.warning(e.getMessage());
        }

        return stocks;
    }

    public boolean canBuy(HashMap<String, Stock> stocks, String stock, int amount) {
        if(!stocks.containsKey(stock)) {
            System.out.println("Invalid stock name, please resubmit");
            return false;
        } else if (stocks.get(stock).getQuantity() < amount || amount <= 0) {
            System.out.println("Invalid quantity, please resubmit");
            return false;
        } else if (amount * stocks.get(stock).getPrice() > balance) {
            System.out.println("You cannot afford " + amount + " " + stock + " stocks");
            System.out.println("The maximum number you can afford with £" + balance
                    + " is " + balance/stocks.get(stock).getPrice());
            return false;
        } else {
            return true;
        }

    }

    /**
     * Request and output available stocks
     * Ask to enter valid name (a valid key in HashMap)
     * Ask to enter valid, affordable amount (stored in tuple value in HashMap
     * Send 0, stock name, and amount to
     */
    public void buyStock(String name, int quantity) {
        try {
            HashMap<String, Stock> stocks = getStocksFromMarket();

            if (!canBuy(stocks, name, quantity))
                return;

            connection.getOutputStream().writeInt(Command.BUY.getValue());
            connection.getOutputStream().writeBytes(name + "\n");
            connection.getOutputStream().writeInt(quantity);

            connection.getOutputStream().flush();

            boolean response = connection.getInputStream().readInt() == 1;
            int responseCode = connection.getInputStream().readInt();

            if(response) {
                updateStock(new Stock(name, quantity, stocks.get(name).getPrice()));
                updateBalance(quantity * stocks.get(name).getPrice() * -1);
            } else {
                handleResponse("buyStock", responseCode);
            }
        } catch (Exception e) {
            System.out.println("Unable to buy stock from the market.");
            LOGGER.warning(e.getMessage());
        }

    }

    public boolean canSell(String name, int quantity) {
        if(!ownedStocks.containsKey(name)) {
            System.out.println("You do not own any " + name + " stocks, please resubmit");
            return false;
        } else if (quantity > ownedStocks.get(name).getQuantity() || quantity <= 0) {
            System.out.println("You do not own " + quantity + " " + name + "stocks, please resubmit");
            return false;
        } else {
            return true;
        }
    }

    public void sellStock(String name, int quantity) {
        try {
            if (!canSell(name, quantity))
                return;

            connection.getOutputStream().writeInt(Command.SELL.getValue());
            connection.getOutputStream().writeBytes(name + "\n");
            connection.getOutputStream().writeInt(quantity);

            connection.getOutputStream().flush();

            boolean response = connection.getInputStream().readInt() == 1;
            int responseCode = connection.getInputStream().readInt();

            if(response) {
                updateStock(new Stock(name, quantity * -1, ownedStocks.get(name).getPrice()));
                updateBalance(quantity * ownedStocks.get(name).getPrice());
            } else {
                handleResponse("sellStock", responseCode);
            }
        } catch (Exception e) {
            System.out.println("Unable to sell stock from the market.");
            LOGGER.warning(e.getMessage());
        }
    }

    public void viewStockList() {
        HashMap<String, Stock> stocks = getStocksFromMarket();

        System.out.println("|⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻ STOCK  MARKET ⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻⎻|");
        System.out.println("|                                     |");
        System.out.println("|~~~~~~~~~~~~ Stock  List ~~~~~~~~~~~~|");
        System.out.println("|                                     |");
        System.out.println("| Name      Quantity         Price(£) |");
        for (Stock stock : stocks.values()) {
            System.out.println(
                    "| " + stock.getName() +
                    "\t\t\t" + stock.getQuantity() +
                    "\t\t\t " + stock.getPrice()
            );
        }
        System.out.println("|_____________________________________|\n");
    }

    public void printBalance() {
        System.out.println("Your Balance: " + balance + "\n");
    }

    private void handleResponse(String method, int responseCode) {
        switch (method + ":" + responseCode) {
            case "buyStock:2":
                System.out.println("Not enough stocks available. Please resubmit.");
                break;
            case "sellStock:2":
                System.out.println("You do not have enough stocks to sell, please try again.");
                break;
            case "buyStock:3":
            case "sellStock:3":
                System.out.println("Invalid Option");
                break;
        }
    }

    public static void main(String args[]) {
        try {
            new Client("0.0.0.0", 43590).run();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}