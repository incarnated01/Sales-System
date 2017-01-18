package com.theironyard.charlotte;

import java.sql.*;
import java.util.List;

/**
 * Created by stephenwilliamson on 1/15/17.
 */
public class User {
    private Integer id;
    private String name;
    private String email;

    public User(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public User(Integer id, String name, String email) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void createUsersTable() {
        try {
            Statement stmt = Main.conn.createStatement();
            stmt.execute("create table if not exists users (id identity, name varchar, email varchar)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static User getUserById(Integer id) {
        User u = null;

        if (id != null) {
            try {
                PreparedStatement stmt = Main.conn.prepareStatement("select * from users where id = ?");
                stmt.setInt(1, id);
                ResultSet results = stmt.executeQuery();
                if (results.next()) {
                    u = new User(results.getInt("id"), results.getString("name"), results.getString("email"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return u;
    }

    public static User getUserByEmail(String email) {
        User u = null;

        try {
            PreparedStatement stmt = Main.conn.prepareStatement("select * from users where email = ?");
            stmt.setString(1, email);

            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                u = new User(results.getInt("id"), results.getString("name"), results.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return u;
    }

    public static Order currentOpenOrder(User user) {
        Order o = null;

        try {
            PreparedStatement stmt = Main.conn.prepareStatement("select top 1 * from orders where user_id = ? and open = true");

            stmt.setInt(1, user.getId());

            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                o = new Order(results.getInt("id"), user.getId(), true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return o;
    }
}
