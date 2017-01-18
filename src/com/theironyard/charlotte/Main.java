package com.theironyard.charlotte;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {
    private static HashMap users = new HashMap();

    static {
        users.put("name@address.com", new User("Alice", "name@address.com"));
    }

    public static void main(String[] args) {
        Spark.get("/", ((req, res) -> {
            HashMap model = new HashMap();
            Session session = req.session();

            if (session.attribute("user") != null) {
                model.put("user", session.attribute("user"));
            }

            return new ModelAndView(model, "home.html");
        }), new MustacheTemplateEngine());

        Spark.get("/login", ((req, res) -> {
            return new ModelAndView(new HashMap(), "login.html");
        }), new MustacheTemplateEngine());

        Spark.post("/login", (req, res) -> {
            Session session = req.session();
            if (users.containsKey(req.queryParams("email"))) {
                session.attribute("user", users.get(req.queryParams("email")));
            }
            res.redirect("/");
            return "";
        });
    }
}
