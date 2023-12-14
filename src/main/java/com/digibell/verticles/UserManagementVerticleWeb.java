package com.digibell.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserManagementVerticleWeb extends AbstractVerticle {

    final List<JsonObject> users = new ArrayList<>(Arrays.asList(
            new JsonObject().put("id", 1).put("name", "John").put("password", "ABC"),
            new JsonObject().put("id", 2).put("name", "Peter").put("password", "XYZ"),
            new JsonObject().put("id", 3).put("name", "Robert").put("password", "XYZ")
    ));
    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.GET, "/").handler(routingContext -> {
            routingContext.response()
                    .putHeader("content-type", "text/plain")
                    .end("Welcome digibell- you are on home now!");
        });

        router.route(HttpMethod.GET, "/api/v1/users").handler(routingContext -> {
            routingContext.response()
                    .setStatusCode(200)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(new JsonArray(getAllUsers()).encode());
        });

        router.route(HttpMethod.GET, "/api/v1/users/:id").handler(routingContext -> {

            Integer id = Integer.parseInt(routingContext.pathParams().get("id"));
            Optional<JsonObject> users = getAllUsers()
                    .stream()
                    .filter(p -> p.getInteger("id").equals(id))
                    .findFirst();
            if (users.isPresent())
                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(users.get().encode());
            else
                routingContext.fail(404, new Exception("User not found"));
        });


        router.route(HttpMethod.POST, "/api/v1/users").handler(routingContext -> {
            JsonObject user = routingContext.getBodyAsJson();
            addUser(user);
            routingContext
                    .response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setStatusCode(200)
                    .end(user.encode());
        });

        router.errorHandler(404, routingContext -> {
            JsonObject errorObject = new JsonObject()
                    .put("code", 404)
                    .put("message",
                            (routingContext.failure() != null) ?
                                    routingContext.failure().getMessage() :
                                    "Not Found"
                    );
            routingContext
                    .response()
                    .setStatusCode(404)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(errorObject.encode());
        });

        router.errorHandler(400, routingContext -> {
            JsonObject errorObject = new JsonObject()
                    .put("code", 400)
                    .put("message",
                            (routingContext.failure() != null) ?
                                    routingContext.failure().getMessage() :
                                    "Validation Exception"
                    );
            routingContext
                    .response()
                    .setStatusCode(400)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(errorObject.encode());
        });

        server.requestHandler(router);
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
