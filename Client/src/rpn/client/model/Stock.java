package rpn.client.model;

/**
 * Represents a stock object which contains the name, quantity and price.
 */
public class Stock {

    private String name;
    private int quantity;
    private int price;

    public Stock(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }
}
