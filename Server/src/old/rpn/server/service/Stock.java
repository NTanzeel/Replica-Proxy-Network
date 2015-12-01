package old.rpn.server.service;

/**
 * Created by jordan on 22/11/2015.
 */
public class Stock {

    private int quantity;
    private int price;

    public Stock(int quantity, int price) {
        this.quantity = quantity;
        this.price = price;
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

    public void setPrice(int price) {
        this.price = price;
    }
}
