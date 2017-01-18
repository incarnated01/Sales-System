package com.theironyard.charlotte;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
        public static Connection conn;

        static {
            try {
                conn = DriverManager.getConnection("jdbc:h2:./main");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void initializeDatabase() {
            User.createUsersTable();
            Item.createItemsTable();
            Order.createOrdersTable();
        }

        public static void main(String[] args) throws SQLException {
            Server.createWebServer().start();
            initializeDatabase();

            Spark.get("/", (request, response) -> {
                HashMap model = new HashMap();
                Session session = request.session();

                // ask our current session if there is a valid user_id.
                User u = User.getUserById(session.attribute("user_id"));

                if (u != null) {
                    List<Item> items = Order.getItemsForOrder(User.currentOpenOrder(u));

                    model.put("items", items);
                    model.put("user", u);
                }

                return new ModelAndView(model, "home.html");
            }, new MustacheTemplateEngine());

            Spark.get("/login", (request, response) -> {
                return new ModelAndView(new HashMap(), "login.html");
            }, new MustacheTemplateEngine());

            Spark.post("/login", (request, response) -> {
                Session session = request.session();

                User u = User.getUserByEmail(request.queryParams("email"));

                if (u != null) {
                    session.attribute("user_id", u.getId());
                }

                response.redirect("/");
                return "";
            });

            Spark.post("/add-item", (request, response) -> {
                // if the user is logged in.
                // see if the user has a valid order
                User currentUser = User.getUserById(request.session().attribute("user_id"));

                if (currentUser != null) {
                    Order o = User.currentOpenOrder(currentUser);

                    if (o == null) {
                        // if they don't have a valid order, make one.
                        o = new Order(currentUser.getId(), true);
                        Order.insertOrder(o);
                    }

                    // add item to the valid order.
                    Order.addItemToOrder(
                            o,
                            new Item(request.queryParams("name"),
                                    Integer.valueOf(request.queryParams("quantity")),
                                    Double.valueOf(request.queryParams("price"))));
                }

                response.redirect("/");
                return "";
            });

            Spark.post("/checkout", (request, response) -> {
                User currentUser = User.getUserById(request.session().attribute("user_id"));
                HashMap model = new HashMap();

                if (currentUser != null) {
                    Order o = User.currentOpenOrder(currentUser);
                    List<Item> items = Order.getItemsForOrder(o);
                    double subTotal =
                            items.stream().collect(Collectors.summingDouble(i -> i.getQuantity() * i.getPrice()));

                    model.put("user", currentUser);
                    model.put("items", items);
                    model.put("subtotal", subTotal);

                    o.setOpen(false);
                    Order.updateOrder(o);
                }

                return new ModelAndView(model, "checkout.html");
            }, new MustacheTemplateEngine());
        }
    }