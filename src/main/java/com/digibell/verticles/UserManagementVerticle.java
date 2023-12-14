package com.digibell.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserManagementVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        RouterBuilder.create(this.vertx, "user-management.yaml")
                .onSuccess(routerBuilder -> {
                    // Add routes handlers
                    // tag::listUSersHandler[]
                    routerBuilder.operation("listUsers").handler(routingContext ->
                            routingContext
                                    .response() // <1>
                                    .setStatusCode(200)
                                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json") // <2>
                                    .end(new JsonArray(getAllUsers()).encode()) // <3>
                    );
                    // end::listUSersHandler[]

                    // tag::createUSersHandler[]
                    routerBuilder.operation("createUser").handler(routingContext -> {
                        RequestParameters params = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY); // <1>
                        JsonObject user = params.body().getJsonObject(); // <2>
                        addUser(user);
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .end(user.encode()); // <3>
                    });
                    // end::createUSersHandler[]
                    // tag::showUSerByIdHandler[]
                    routerBuilder.operation("showUserById").handler(routingContext -> {
                        RequestParameters params = routingContext.get("parsedParameters"); // <1>
                        Integer id = params.pathParameter("userId").getInteger(); // <2>
                        Optional<JsonObject> users = getAllUsers()
                                .stream()
                                .filter(p -> p.getInteger("id").equals(id))
                                .findFirst(); // <3>
                        if (users.isPresent())
                            routingContext
                                    .response()
                                    .setStatusCode(200)
                                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                    .end(users.get().encode()); // <4>
                        else
                            routingContext.fail(404, new Exception("User not found")); // <5>
                    });
                    // end::showUSerByIdHandler[]

                    // Generate the router
                    // tag::routerGen[]
                    Router router = routerBuilder.createRouter(); // <1>
                    router.errorHandler(404, routingContext -> { // <2>
                        JsonObject errorObject = new JsonObject() // <3>
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
                                .end(errorObject.encode()); // <4>
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

                    server = vertx.createHttpServer(new HttpServerOptions().setPort(8091).setHost("localhost")); // <5>
                    server.requestHandler(router).listen(); // <6>
                    // end::routerGen[]
                    startPromise.complete(); // Complete the verticle start
                })
                .onFailure(startPromise::fail);
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

    // tag::loadSpecSampleMethod[]
    // For documentation purpose
    private void loadSpecSample(Promise<Void> startPromise) {
        // tag::loadSpec[]
        RouterBuilder.create(this.vertx, "user-management.yaml")
                .onSuccess(routerBuilder -> { // <1>
                    // You can start building the router using routerBuilder
                }).onFailure(cause -> { // <2>
                    // Something went wrong during router factory initialization
                    startPromise.fail(cause);
                });
        // end::loadSpec[]
    }
    // end::loadSpecSampleMethod[]

    final List<JsonObject> users = new ArrayList<>(Arrays.asList(
            new JsonObject().put("id", 1).put("name", "Fufi").put("password", "ABC"),
            new JsonObject().put("id", 2).put("name", "Garfield").put("password", "XYZ"),
            new JsonObject().put("id", 3).put("name", "Puffa").put("password", "XYZ")
    ));
}
