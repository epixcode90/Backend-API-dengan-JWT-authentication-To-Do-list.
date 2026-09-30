package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.User;
import com.example.auth.service.AuthService;
import com.example.auth.util.JsonUtil;
import io.javalin.http.Context;

import java.util.Map;

public class AuthController {

    private static final AuthService service = new AuthService();

    public static void register(Context ctx){
        try{
            RegisterRequest req = JsonUtil.GSON.fromJson(ctx.body() , RegisterRequest.class);
            User user = service.register(req);

            ctx.status(201).json(Map.of(
                    "message", "Register successfully!",
                    "userid",user.getId(),
                    "username",user.getUsername()));
        }catch (IllegalArgumentException e){
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    public static void login(Context ctx){
        try {
            LoginRequest req = JsonUtil.GSON.fromJson(ctx.body() , LoginRequest.class);
            ctx.json(service.login(req));
        }catch (IllegalArgumentException e){
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    public static void refresh(Context ctx) {
        try {
            RefreshRequest req = JsonUtil.GSON.fromJson(ctx.body(), RefreshRequest.class);
            ctx.json(service.refresh(req));
        } catch (IllegalArgumentException e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }

    public static void logout(Context ctx) {
        try {
            RefreshRequest req = JsonUtil.GSON.fromJson(ctx.body(), RefreshRequest.class);
            service.logout(req);
            ctx.json(Map.of("message", "Logout sukses"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
}
