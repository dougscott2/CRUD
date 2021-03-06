package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException{  //method to create tables
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS games (id IDENTITY, title VARCHAR, system VARCHAR)");

    }
    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser (Connection conn, String name) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            user = new User();
            user.id = results.getInt("id");
            user.password = results.getString("password");
        }
        return user;
    }



    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);



        ArrayList<Game> games = new ArrayList();
       HashMap<String, User> users = new HashMap();
       // final User DOUG = new User(); //test for passwords
       // DOUG.password="1234";
        //users.put("DOUG", DOUG);

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    m.put("username", username);
                    m.put("games", games);
                    if (username == null){
                        return new ModelAndView(m, "not-logged-in.html");
                    }
                    return new ModelAndView(m, "logged-in.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                   if (username.isEmpty() || password.isEmpty()){
                       Spark.halt(403);
                   }
                    User user = users.get(username);
                    if (user == null){
                        user = new User();
                        user.password = password;
                        users.put(username, user);
                    } else if (!password.equals(user.password)){
                        Spark.halt(403);
                    }
                    Session session = request.session();
                    session.attribute("username", username);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/add-game",
                ((request, response) -> {
                    //Session session = request.session();
                  Game game = new Game();
                      game.id = games.size() + 1;
                      game.title = request.queryParams("newGame");
                      game.system = request.queryParams("system");
                      games.add(game);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.get(
                "/delete-game",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    try {
                        int idNum = Integer.valueOf(id);
                        games.remove(idNum - 1);
                        for (int i = 0; i < games.size(); i++) {
                           games.get(i).id = i + 1;
                        }
                    } catch (Exception e){}
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
                "/edit-game",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    String id = request.queryParams("id");
                    m.put("id", id);
                    return new ModelAndView(m, "/edit-game.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-game",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    try{
                        int idNum = Integer.valueOf(id);
                        //Game game = games.get(idNum - 1);
                        //game.title = request.queryParams("newGame");
                        games.get(idNum-1).title = request.queryParams("editGame");
                        games.get(idNum-1).system = request.queryParams("editSystem");
                    }
                    catch (Exception e){

                    }
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

    }
}
