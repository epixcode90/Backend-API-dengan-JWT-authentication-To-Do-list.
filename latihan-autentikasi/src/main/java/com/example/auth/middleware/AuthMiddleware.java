package com.example.auth.middleware;

import com.example.auth.util.JwtUtil;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import io.jsonwebtoken.Claims;


import java.util.Map;

public class AuthMiddleware {

    public static void requireAuth(Context ctx){

        String header = ctx.header("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedResponse("Authorization header is invalid");
        }

        String token = header.substring(7);

        try{
            Claims claims = JwtUtil.parseToken(token);

            Number userIdNum = claims.get("userId", Number.class);
            Long userId =  userIdNum !=null ? userIdNum.longValue() : null;

            ctx.attribute("userId", userId);
            ctx.attribute("username",claims.getSubject());
            ctx.attribute("role",claims.get("role",String.class));

        }catch (Exception e){
            throw new UnauthorizedResponse("invalid token");
        }

    }


    public static io.javalin.http.Handler requireRoleHandler(String requireRole){
        return ctx -> {
            String userRole = ctx.attribute("role");
            if (!requireRole.equals(userRole)) {
                ctx.status(403).json(Map.of("error","Forbidden"));
                throw new ForbiddenResponse();
            }
        };
    }
}
