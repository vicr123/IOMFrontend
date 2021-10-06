package com.vicr123.IOMFrontend.Server;

import express.http.HttpRequestHandler;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;

import java.io.IOException;

public class StaticFilesMiddleware implements HttpRequestHandler {
    @Override
    public void handle(Request req, Response res) {
        if (req.getHeader("accept").contains("text/html")) {
            String path = req.getPath();
            if (path.equals("/")) path = "/index.html";

            try {
                res.sendBytes(getClass().getClassLoader().getResourceAsStream("/frontend/build" + path).readAllBytes());
            } catch (IOException e) {
                res.sendStatus(Status._404);
            }
        }
    }
}
