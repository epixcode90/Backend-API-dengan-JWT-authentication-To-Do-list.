package com.example.auth.controller;

import com.example.auth.dto.CreateTaskRequest;
import com.example.auth.dto.UpdateTaskRequest;
import com.example.auth.service.TaskService;
import com.example.auth.util.JsonUtil;
import io.javalin.http.Context;

import java.util.Map;

public class TaskController {

    private static final TaskService service = new TaskService();


    public static void create(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
                return;
            }

            CreateTaskRequest request = JsonUtil.GSON.fromJson(ctx.body(), CreateTaskRequest.class);

            var respons = service.create(userId, request);

            ctx.status(201).json(respons);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }


    public static void listAll(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
            }

            var respons = service.listAll(userId);
            ctx.json(respons);
        } catch (IllegalArgumentException e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    public static void getOne(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
                return;
            }

            Long taskId = Long.parseLong(ctx.pathParam("id"));

            var respons = service.getOne(userId, taskId);
            ctx.json(respons);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "id tidak valid"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    public static void update(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
            }
            Long taskId = Long.parseLong(ctx.pathParam("id"));

            UpdateTaskRequest request = JsonUtil.GSON.fromJson(ctx.body(), UpdateTaskRequest.class);

            var respons = service.update(userId, taskId, request);
            ctx.json(respons);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "id tidak valid"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }

    public static void delete(Context ctx) {
        try {
            Long userId = ctx.attribute("userId");
            if (userId == null) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
            }
            Long taskId = Long.parseLong(ctx.pathParam("id"));
            service.delete(userId, taskId);
            ctx.json(Map.of("message", "task deleted"));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "id tidak valid"));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
}
