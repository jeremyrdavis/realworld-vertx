package io.vertx.realworld.conduit.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class RegistrationVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> future){
        vertx.createHttpServer()
                .requestHandler(r -> {
                    r.response().end("hello, world");
                })
                .listen(8080, result -> {
                    if(result.succeeded()){
                        future.complete();
                    }else{
                        future.fail(result.cause());
                    }
                });
    }
}
