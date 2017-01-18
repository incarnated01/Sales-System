package com.theironyard.charlotte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by stephenwilliamson on 1/15/17.
 */
public class Item {
    private int id;
    private String name;
    private int quantity;
    private double price;

    public Item(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static void createItemsTable() {
        try {
            Statement stmt = Main.conn.createStatement();

            stmt.execute("create table if not exists items (id identity, name varchar, quantity int, price double, order_id int)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}