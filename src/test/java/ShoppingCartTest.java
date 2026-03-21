
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ShoppingCartTest {

    @Test
    public void testSingleItem() {
        Main.ShoppingCart cart = new Main.ShoppingCart();
        
        cart.addItem(10.50, 2);
        
        assertEquals(21.0, cart.getTotal(), "The total should be exactly 21.0");
    }

    @Test
    public void testMultipleItemsTotal() {
        Main.ShoppingCart cart = new Main.ShoppingCart();

        cart.addItem(10.00, 2); // 20.00
        cart.addItem(5.50, 3);  // 16.50

        assertEquals(36.5, cart.getTotal(), 0.0001, "Total should include all items");
    }

    @Test
    public void testZeroQuantityItemDoesNotChangeTotal() {
        Main.ShoppingCart cart = new Main.ShoppingCart();

        cart.addItem(10.00, 1);
        cart.addItem(99.99, 0);

        assertEquals(10.0, cart.getTotal(), 0.0001, "Zero quantity should not affect total");
    }
}