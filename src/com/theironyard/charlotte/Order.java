package com.theironyard.charlotte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephenwilliamson on 1/15/17.
 */
public class Order {

    Integer id;
    Integer userId;
    List<Item> items;

    public Order() {
    }

    public Order(Integer userId, boolean complete) {
        this.userId = userId;
    }

    public Order(Integer id, Integer userId, boolean complete) {
        this.id = id;
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    private static List<Item> getItemsforCurrentOrder(Connection connection, Integer orderId) throws SQLException {
        List<Item> items = new ArrayList<>();

        if (orderId != null) {
            PreparedStatement stmt = connection.prepareStatement("select * from items where order_id = ?");
            stmt.setInt(1, orderId);

            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                String name = results.getString("name");
                Integer quantity = results.getInt("quantity");
                Double price = results.getDouble("price");
                Integer currentOrder = orderId;
                items.add(new Item(name, quantity, price, currentOrder));
            }
        }
        return items;
    }

    public static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS orders (id IDENTITY, user_id INT)");
    }

    public static int createOrder(Connection conn, int userID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("insert into orders values (null, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, userID);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();

        keys.next();

        return keys.getInt(1);
    }

    public static Order getLatestCurrentOrder(Connection conn, Integer userId) throws SQLException {
        Order order = null;

        if (userId != null) {
            PreparedStatement stmt = conn.prepareStatement("select top 1 * from orders where user_id = ? and complete = false");
            stmt.setInt(1, userId);
            ResultSet results  = stmt.executeQuery();

            if (results.next()) {
                order = new Order(results.getInt("id"), results.getInt("user_id"), false);
            }
        }
        return order;
    }
}