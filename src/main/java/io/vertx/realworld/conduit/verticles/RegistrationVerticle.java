package io.vertx.realworld.conduit.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.realworld.conduit.domain.ConduitUser;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;

public class RegistrationVerticle extends AbstractVerticle{

    private MongoClient mongoClient;

    public static final String COLLECTION = "conduit_users";

    @Override
    public void start(Future<Void> fut) {

        System.out.println(config());
        // Create a MongoDB client
        mongoClient = MongoClient.createShared(vertx, config());

        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message
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

        System.out.println(routingContext.getBodyAsString());
        System.out.println("input:");
        System.out.println(routingContext.getBodyAsJson());

        JsonObject jsonConduitUser = routingContext.getBodyAsJson();

        ConduitUser conduitUser = new ConduitUser(jsonConduitUser);

        // Validation
        EmailValidator emailValidator = EmailValidator.getInstance();

        // if the email address is invalid
        if(!emailValidator.isValid(conduitUser.getEmail())){
            System.out.println("invalid email detected");
            ValidationError validationError = new ValidationError("invalid email address");
            routingContext.response().setStatusCode(422)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(validationError));
        }

        // return the newly created user
        System.out.println("saving user");
        mongoClient.insert(COLLECTION, conduitUser.toJson(), r ->{
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(new ConduitUser("conduituser@vertx.io", "conduitusername", "conduituserpassword", "I am a test user", null, null, null)));
        });

    }

    private void createSomeData(Handler<AsyncResult<Void>> next, Future<Void> fut) {

        ArrayList<ConduitUser> conduitUsers = new ArrayList<ConduitUser>(3);
        conduitUsers.add(new ConduitUser("user1@vertx.io", "user1", "password1", null, null, null, null));
        conduitUsers.add(new ConduitUser("user2@vertx.io", "user1", "password2", null, null, null, null));
        conduitUsers.add(new ConduitUser("user3@vertx.io", "user1", "password3", null, null, null, null));

        mongoClient.count(COLLECTION, new JsonObject(), count ->{
            if(count.succeeded()){
                if(count.result() <= 0){
                    conduitUsers.forEach( u -> { mongoClient.insert(COLLECTION, u.toJson(), ar -> {
                        if(ar.failed()){
                            fut.fail(ar.cause());
                        }else{
                            next.handle(Future.<Void>succeededFuture());
                        }
                    });});
                }
            }
        });
   }

}
