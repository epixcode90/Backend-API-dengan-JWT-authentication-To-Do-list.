package com.example.auth;

import com.example.auth.config.Database;
import com.example.auth.controller.AuthController;
import com.example.auth.controller.TaskController;
import com.example.auth.middleware.AuthMiddleware;
import com.example.auth.util.JsonUtil;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String[] args) {

        Database.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->  {
            System.out.println("\nShutting down");
            Database.shutdown();
        }));

        Javalin app = Javalin.create(config ->{
          config.showJavalinBanner = false;
          config.jsonMapper(new io.javalin.json.JsonMapper(){

              @Override
              public String toJsonString(Object obj, java.lang.reflect.Type type) {
                  return JsonUtil.toJson(obj);
              }

              @Override
              public <T> T fromJsonString(String json,  java.lang.reflect.Type targetType) {
                  return JsonUtil.GSON.fromJson(json, targetType);
              }
          });
        } );


        app.post("/api/auth/login", AuthController::login);
        app.post("/api/auth/register", AuthController::register);
        app.get("/health",ctx -> ctx.json(Map.of("status", "UP")));

        app.post("/api/auth/refresh", AuthController::refresh);
        app.post("/api/auth/logout",  AuthController::logout);

        app.before("/api/user/profile", AuthMiddleware::requireAuth);
        app.get("/api/user/profile", ctx -> {
            Map<String, Object> res = new HashMap<>();
            res.put("userId",ctx.attribute("userId"));
            res.put("username",ctx.attribute("username"));
            res.put("role",ctx.attribute("role"));
            res.put("massage","selamat datang di halaman profile");
            ctx.json(res);
        });

        app.before("/api/tasks",   AuthMiddleware::requireAuth);
        app.before("/api/tasks/*", AuthMiddleware::requireAuth);

        app.post(  "/api/tasks",      TaskController::create);
        app.get(   "/api/tasks",      TaskController::listAll);
        app.get(   "/api/tasks/{id}", TaskController::getOne);
        app.put(   "/api/tasks/{id}", TaskController::update);
        app.delete("/api/tasks/{id}", TaskController::delete);

        app.before("/api/admin/dashboard", AuthMiddleware::requireAuth);
        app.before("/api/admin/dashboard", AuthMiddleware.requireRoleHandler("ADMIN"));
        app.get("/api/admin/dashboard", ctx -> {
            Map<String, Object> res = new HashMap<>();
            res.put("message","halo admin di halaman profile");
            ctx.json(res);
        });

        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("[ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            ctx.status(500).json(Map.of("error", "internal server error"));
        });

        app.start(7000);
        System.out.println("server di http/localhost:7000");

    }

}
