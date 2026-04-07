package sep2.shoppingcart.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CartService {

    public void saveCart(int totalItems, double totalCost, String language, List<CartItemData> items) {
        Connection connection = null;
        String insertRecordSql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String insertItemSql = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            int cartRecordId;
            try (PreparedStatement recordStatement = connection.prepareStatement(insertRecordSql, Statement.RETURN_GENERATED_KEYS)) {
                recordStatement.setInt(1, totalItems);
                recordStatement.setDouble(2, totalCost);
                recordStatement.setString(3, language);
                recordStatement.executeUpdate();

                try (ResultSet keys = recordStatement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Cart record ID was not generated");
                    }
                    cartRecordId = keys.getInt(1);
                }
            }

            try (PreparedStatement itemStatement = connection.prepareStatement(insertItemSql)) {
                for (CartItemData item : items) {
                    itemStatement.setInt(1, cartRecordId);
                    itemStatement.setInt(2, item.itemNumber());
                    itemStatement.setDouble(3, item.price());
                    itemStatement.setInt(4, item.quantity());
                    itemStatement.setDouble(5, item.subtotal());
                    itemStatement.addBatch();
                }
                itemStatement.executeBatch();
            }

            connection.commit();
        } catch (SQLException ex) {
            rollback(connection);
            throw new RuntimeException("Unable to save cart data", ex);
        } finally {
            close(connection);
        }
    }

    private void rollback(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}