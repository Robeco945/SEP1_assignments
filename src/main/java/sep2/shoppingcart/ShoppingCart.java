package sep2.shoppingcart;

public class ShoppingCart {
    private double total;

    public double calculateItemCost(double price, int quantity) {
        return price * quantity;
    }

    public void addItem(double price, int quantity) {
        total += calculateItemCost(price, quantity);
    }

    public double getTotal() {
        return total;
    }
}
