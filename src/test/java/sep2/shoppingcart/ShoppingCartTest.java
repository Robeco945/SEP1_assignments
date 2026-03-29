
package sep2.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ShoppingCartTest {

    @Test
    public void testItemCostCalculation() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(42.0, cart.calculateItemCost(10.5, 4), 0.0001,
                "Item cost should be price multiplied by quantity");
    }

    @Test
    public void testSingleItem() {
        ShoppingCart cart = new ShoppingCart();
        
        cart.addItem(10.50, 2);
        
        assertEquals(21.0, cart.getTotal(), "The total should be exactly 21.0");
    }

    @Test
    public void testMultipleItemsTotal() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem(10.00, 2); // 20.00
        cart.addItem(5.50, 3);  // 16.50

        assertEquals(36.5, cart.getTotal(), 0.0001, "Total should include all items");
    }

    @Test
    public void testZeroQuantityItemDoesNotChangeTotal() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem(10.00, 1);
        cart.addItem(99.99, 0);

        assertEquals(10.0, cart.getTotal(), 0.0001, "Zero quantity should not affect total");
    }
}