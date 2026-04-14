package sep2.shoppingcart.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceTest {

    private static final String INSERT_RECORD_SQL =
            "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
    private static final String INSERT_ITEM_SQL =
            "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

    @Test
    void saveCartPersistsRecordAndItems() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement recordStatement = mock(PreparedStatement.class);
        PreparedStatement itemStatement = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(connection.prepareStatement(eq(INSERT_RECORD_SQL), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(recordStatement);
        when(connection.prepareStatement(eq(INSERT_ITEM_SQL))).thenReturn(itemStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(7);

        List<CartItemData> items = List.of(
                new CartItemData(1, 10.0, 2, 20.0),
                new CartItemData(2, 1.5, 4, 6.0)
        );

        CartService service = new CartService();
        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(connection);
            service.saveCart(6, 26.0, "en_US", items);
        }

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(true);
        verify(connection).close();

        verify(recordStatement).setInt(1, 6);
        verify(recordStatement).setDouble(2, 26.0);
        verify(recordStatement).setString(3, "en_US");
        verify(recordStatement).executeUpdate();

        verify(itemStatement, times(2)).addBatch();
        verify(itemStatement).executeBatch();
        verify(itemStatement, times(2)).setInt(1, 7);
    }

    @Test
    void saveCartRollsBackWhenBatchExecutionFails() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement recordStatement = mock(PreparedStatement.class);
        PreparedStatement itemStatement = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(connection.prepareStatement(eq(INSERT_RECORD_SQL), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(recordStatement);
        when(connection.prepareStatement(eq(INSERT_ITEM_SQL))).thenReturn(itemStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(3);
        when(itemStatement.executeBatch()).thenThrow(new SQLException("write failed"));

        CartService service = new CartService();
        RuntimeException thrown;
        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(connection);
            thrown = assertThrows(RuntimeException.class,
                    () -> service.saveCart(1, 10.0, "en_US", List.of(new CartItemData(1, 10.0, 1, 10.0))));
        }

        assertTrue(thrown.getMessage().contains("Unable to save cart data"));
        assertNotNull(thrown.getCause());
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
        verify(connection).close();
    }

    @Test
    void saveCartWrapsConnectionFailure() throws Exception {
        CartService service = new CartService();

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("connection refused"));

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> service.saveCart(1, 5.0, "en_US", List.of()));

            assertTrue(thrown.getMessage().contains("Unable to save cart data"));
            assertNotNull(thrown.getCause());
        }
    }
}
