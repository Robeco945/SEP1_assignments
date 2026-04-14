package sep2.shoppingcart.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartItemDataTest {

    @Test
    void recordStoresExpectedValues() {
        CartItemData data = new CartItemData(2, 10.5, 4, 42.0);

        assertEquals(2, data.itemNumber());
        assertEquals(10.5, data.price());
        assertEquals(4, data.quantity());
        assertEquals(42.0, data.subtotal());
    }
}
