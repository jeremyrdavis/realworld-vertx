package io.vertx.realworld.conduit.verticles;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.realworld.conduit.domain.User;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class UsersRegistrationVerticleTest extends BaseVerticleTest {

    @Before
    public void setUp(TestContext testContext){
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", HTTP_PORT)
                        .put("db_name", "conduit_users")
                        .put("connection_string", "mongodb://localhost:" + MONGO_PORT));
        vertx = Vertx.vertx();
        vertx.deployVerticle(UsersVerticle.class.getName(), options, testContext.asyncAssertSuccess());
        this.endpoint = "/api/users";
    }

    /**
     * This tests the successful post to the Registration endpoint, "/api/users"
     *
     * POST /api/users
     *
     * payload:
     * {
     *  "user":{
     *      "username": "Jacob",
     *      "email": "jake@jake.jake",
     *      "password": "jakejake"
     *      }
     *  }
     *
     * expected response:
     * {
     *   "user": {
     *       "email": "jake@jake.jake",
     *       "token": "jwt.token.here",
     *       "username": "jake",
     *       "bio": null,
     *       "image": null
     *       }
     *   }
     * @param testContext
     * @see "https://github.com/gothinkster/realworld/tree/master/api"
     */
    @Test
    public void testRegisterNewUser(TestContext testContext){
        LOGGER.debug("testRegisterNewUser");

        final Async async = testContext.async();

        User user = new User("conduituser@vertx.io", "conduitusername", "conduitpassword", null, null, null, null);
        final String payload = Json.encodePrettily(user);

        LOGGER.debug(payload);

        try{
            vertx.createHttpClient().post(8080, "localhost", "/api/users")
                    .putHeader("content-type","application/json; charset=utf-8")
                    .putHeader("content-length", String.valueOf(payload.length()))
                    .handler(
                        response ->{
                            testContext.assertEquals(201, response.statusCode());
                            testContext.assertEquals("application/json; charset=utf-8", response.getHeader("content-type"));
                            response.bodyHandler(body ->{
                                final User conduitUser = Json.decodeValue(body.toString(), User.class);
                                System.out.println(conduitUser.toJson());
                                testContext.assertNotNull(conduitUser);
                                testContext.assertNotNull(conduitUser.getId());
                                testContext.assertEquals("conduituser@vertx.io", conduitUser.getEmail());
                                testContext.assertEquals("conduitusername", conduitUser.getUsername());
                                testContext.assertEquals("conduitpassword", conduitUser.getPassword());
                                async.complete();
                            });
                        })
                    .write(payload)
                    .end();
        }catch (Exception e){
            assertNull(e);
        }


    }

    @Test
    public void testValidationForEmailWithoutDomain(TestContext testContext){
        User user = new User("conduituser", "username", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testValidationForEmailWithInvalidDomain(TestContext testContext){
        User user = new User("conduituser@i", "username", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testValidationForEmptyEmailAddress(TestContext testContext){
        User user = new User(" ", "username", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testValidationForNullEmailAddress(TestContext testContext){
        User user = new User(null, "username", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }


    /**
     * This test case verifies that an error is thrown by the Registration endpoint, "/api/users" when no value supplied for username
     *
     * @param testContext
     */
    @Test
    public void testValidationForNullUsername(TestContext testContext){
        User user = new User("conduituser@vertx.io", null, "conduitpassword");
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_USERNAME_MESSAGE));
    }

    @Test
    public void testValidationForEmptyUsername(TestContext testContext){
        User user = new User("conduituser@vertx.io", "  ", "conduitpassword");
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_USERNAME_MESSAGE));
    }

    /**
     * This test case verifies that an error is thrown by the Registration endpoint, "/api/users" when no value supplied for password
     *
     * @param testContext TestContext
     * @see BaseVerticleTest
     */
    @Test
    public void testValidationForNullPassword(TestContext testContext){
        User user = new User("conduituser@vertx.io", "username", null);
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_PASSWORD_MESSAGE));
    }

    /**
     * This test case verifies that an error is thrown by the Registration endpoint, "/api/users" when an empty String is supplied for password
     *
     * @param testContext TestContext
     * @see BaseVerticleTest
     */
    @Test
    public void testValidationForEmptyPassword(TestContext testContext){
        User user = new User("conduituser@vertx.io", "username", "  ");
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_PASSWORD_MESSAGE));
    }
}
