package com.vicr123.IOMFrontend.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vicr123.IOMFrontend.IOMFrontendPlugin;
import express.filter.Filter;
import express.http.HttpRequestHandler;
import express.http.request.Authorization;
import express.http.request.Request;
import express.http.response.Response;

import java.util.UUID;

public class PlayerMiddleware implements HttpRequestHandler, Filter {
    IOMFrontendPlugin plugin;
    JWTVerifier verifier;

    public PlayerMiddleware(IOMFrontendPlugin plugin, Algorithm algorithm) {
        this.plugin = plugin;
        this.verifier = JWT.require(algorithm)
                .withIssuer("IOM")
                .build();
    }

    @Override
    public void handle(Request req, Response res) {
        if (req.hasAuthorization()) {
            Authorization auth = req.getAuthorization().get(0);
            if (auth.getType().equals("Bearer")) {
                try {
                    DecodedJWT jwt = verifier.verify(auth.getData());
                    String playerUuid = jwt.getClaim("sub").asString();
                    req.addMiddlewareContent(this, plugin.getServer().getPlayer(UUID.fromString(playerUuid)));
                } catch (JWTVerificationException ignored) {

                }
            }
        }
    }

    @Override
    public String getName() {
        return "player";
    }
}
