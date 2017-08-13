package io.vertx.realworld.conduit.verticles;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.realworld.conduit.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class UsersAuthenticationTest extends BaseVerticleTest{

    @Before
    public void setUp(TestContext testContext){
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", HTTP_PORT)
                        .put("db_name", "conduit_users")
                        .put("connection_string", "mongodb://localhost:" + MONGO_PORT)
                        .put("ssl", false));
        vertx = Vertx.vertx();
        vertx.deployVerticle(UsersVerticle.class.getName(), options, testContext.asyncAssertSuccess());
        this.endpoint = "/api/users/login";
    }

    @Test
    public void testNullEmailValidation(TestContext testContext){
        User user = new User(null, "conduitpassword");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testEmptyEmailValidation(TestContext testContext){
        User user = new User("   ", "conduitpassword");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testNullPasswordlValidation(TestContext testContext){
        User user = new User("conduituser@vertx.io", null);
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_PASSWORD_MESSAGE));
    }
    @Test
    public void testEmptyPasswordlValidation(TestContext testContext){
        User user = new User("conduituser@vertx.io", "   ");
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_PASSWORD_MESSAGE));
    }
}
