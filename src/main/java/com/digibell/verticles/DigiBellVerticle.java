package com.digibell.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

public class DigiBellVerticle extends AbstractVerticle {

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        });

        server.listen(8090);
    }
}