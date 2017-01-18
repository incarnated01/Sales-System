package com.theironyard.charlotte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephenwilliamson on 1/15/17.
 */
public class Order {

    private int id;
    private int user_id;
    private boolean open;

    public Order(int id, int user_id, boolean open) {
        this.id = id;
        this.user_id = user_id;
        this.open = open;
    }

    public Order(int user_id, boolean open) {
        this.user_id = user_id;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) { this.id = id; }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public static void createOrdersTable() {
        try {
            Statement stmt = Main.conn.createStatement();

            stmt.execute("create table if not exists orders (id identity, user_id int, open boolean)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertOrder(Order order) {
        try {
            PreparedStatement stmt = Main.conn.prepareStatement("insert into orders values (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUser_id());
            stmt.setBoolean(2, order.isOpen());
            stmt.executeUpdate();

            ResultSet results = stmt.getGeneratedKeys();

            if (results.next()) {
                order.setId(results.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addItemToOrder(Order order, Item item) {
        // insert into items values (NULL, item.getName(), item.getQuantity(), item.getPrice, order.getId());
        try {
            PreparedStatement stmt = Main.conn.prepareStatement("insert into items values (NULL, ?, ?, ?, ?)");
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getQuantity());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, order.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<Item> getItemsForOrder(Order order) {
        List<Item> returnList = null;

        if (order != null) {
            // select * from items where order_id = order.getId();
            try {
                PreparedStatement stmt = Main.conn.prepareStatement("select * from items where order_id = ?");
                stmt.setInt(1, order.getId());

                ResultSet results = stmt.executeQuery();

                while (results.next()) {
                    if (returnList == null) {
                        returnList = new ArrayList<>();
                    }

                    returnList.add(
                            new Item(results.getInt("id"),
                                    results.getString("name"),
                                    results.getInt("quantity"),
                                    results.getDouble("price")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return returnList;
    }

    public static void updateOrder(Order order) {
        // update orders set user_id = ?, open = ? where id = order.getId();
        try {
            PreparedStatement stmt = Main.conn.prepareStatement("update orders set user_id = ?, open = ? where id = ?");
            stmt.setInt(1, order.getUser_id());
            stmt.setBoolean(2, order.isOpen());
            stmt.setInt(3, order.getId());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}