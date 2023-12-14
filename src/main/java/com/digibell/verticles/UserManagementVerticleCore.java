package com.digibell.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.openapi.RouterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserManagementVerticleCore extends AbstractVerticle {

    final List<JsonObject> users = new ArrayList<>(Arrays.asList(
            new JsonObject().put("id", 1).put("name", "John").put("password", "ABC"),
            new JsonObject().put("id", 2).put("name", "Peter").put("password", "XYZ"),
            new JsonObject().put("id", 3).put("name", "Robert").put("password", "XYZ")
    ));
    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(req -> {

            String path = req.path();
            String method = req.method().toString();
            if (path.equals("/api/v1/users") && method.equals("GET")) {
                req.response()
                        .setStatusCode(200)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(new JsonArray(getAllUsers()).encode());
            }

            if (path.startsWith("/api/v1/users/1") && method.equals("GET")) {

                Integer id = Integer.getInteger(req.getParam("id"),1);
                Optional<JsonObject> users = getAllUsers()
                        .stream()
                        .filter(p -> p.getInteger("id").equals(id))
                        .findFirst(); // <3>
                if (users.isPresent())
                    req.response()
                            .setStatusCode(200)
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(users.get().encode());
            }

            if (path.startsWith("/api/v1/users") && method.equals("POST")) {
                req.bodyHandler(buffer -> {
                    JsonObject user = new JsonObject(buffer.toString());
                    addUser(user);
                    req.response()
                            .setStatusCode(200)
                            .end(user.encode());
                });
            }
        });

        server.listen(8091);
    }

    @Override
    public void stop() {
        this.server.close();
    }

    private List<JsonObject> getAllUsers() {
        return this.users;
    }

    private void addUser(JsonObject user) {
        this.users.add(user);
    }
}
