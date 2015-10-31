package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Game> games = new ArrayList();
       HashMap<String, User> users = new HashMap();






        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        return new ModelAndView(new HashMap(), "not-logged-in.html");
                    }
                    HashMap m = new HashMap();
                    m.put("username", username);

                    return new ModelAndView(m, "logged-in.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                   if (username == null || password== null){
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
                "addGame",
                ((request, response) -> {
                    //Session session = request.session();
                    Game game = new Game();
                    game.id=games.size() + 1;
                    game.title= request.queryParams("newGame");
                    game.system = request.queryParams("system");
                    games.add(game);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete-game",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    try {
                        int idNum = Integer.valueOf(id);
                        games.remove(idNum-1);
                        for(int i = 0; i < games.size(); i++){
                            games.get(i).id = i + 1;
                        }
                    } catch (Exception e){

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
