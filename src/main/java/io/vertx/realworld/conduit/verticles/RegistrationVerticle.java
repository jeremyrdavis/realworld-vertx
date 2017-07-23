package io.vertx.realworld.conduit.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.realworld.conduit.domain.User;

public class RegistrationVerticle extends AbstractVerticle{


    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        router.post("/api/users").handler(this::registerUser);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    /**
     * Implements the Realworld API "Registration" endpoint
     * https://github.com/gothinkster/realworld/tree/master/api#registration
     *
     * The JSON posted should resemble:
     * {
     *  "user":{
     *      "username": "Jacob",
     *      "email": "jake@jake.jake",
     *      "password": "jakejake"
     *      }
     * }
     *
     * The response should be the newly registered User object:
     * https://github.com/gothinkster/realworld/tree/master/api#users-for-authentication
     *
     * The payload should resemble:
     * {
     *  "user": {
     *      "email": "jake@jake.jake",
     *      "token": "jwt.token.here",
     *      "username": "jake",
     *      "bio": "I work at statefarm",
     *      "image": null
     *      }
     *  }
     *
     * @param routingContext
     */
    private void registerUser(RoutingContext routingContext){
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(new User("conduituser@vertx.io", "conduitusername", "conduituserpassword", "I am a test user", null, null)));
    }

}
