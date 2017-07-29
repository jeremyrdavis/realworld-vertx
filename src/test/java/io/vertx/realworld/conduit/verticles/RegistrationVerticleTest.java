package io.vertx.realworld.conduit.verticles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.realworld.conduit.domain.User;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(VertxUnitRunner.class)
public class RegistrationVerticleTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationVerticleTest.class);

    private Vertx vertx;

    private static MongodProcess MONGO;

    private static int MONGO_PORT = 12345;

    private static int HTTP_PORT = 8080;

    /**
     * Fire up the Mongo process
     *
     * @throws IOException
     */
    @BeforeClass
    public static void initialize() throws IOException{
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(MONGO_PORT, Network.localhostIsIPv6()))
                .build();
        MongodExecutable mongodExecutable =
                starter.prepare(mongodConfig);
        MONGO = mongodExecutable.start();

    }

    @AfterClass
    public static void shutDown(){ MONGO.stop(); }

    @Before
    public void setUp(TestContext testContext){
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("http.port", HTTP_PORT)
                        .put("db_name", "conduit_users")
                        .put("connection_string", "mongodb://localhost:" + MONGO_PORT));
        vertx = Vertx.vertx();
        vertx.deployVerticle(RegistrationVerticle.class.getName(), options, testContext.asyncAssertSuccess());

        LOGGER.info("info");
        LOGGER.debug("debug");
        LOGGER.error("error");
        LOGGER.trace("trace");
        LOGGER.fatal("fatal");
    }

    @After
    public void tearDown(TestContext testContext){
        vertx.close(testContext.asyncAssertSuccess());
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
    public void testRegisterNewUserValidationForEmail(TestContext testContext){
        LOGGER.debug("testRegisterNewUserValidationForEmail");

        final Async async = testContext.async();
        final String json = Json.encodePrettily(new User(null, "conduituser", "conduitpassword", null, null, null, null));

        vertx.createHttpClient().post(8080, "localhost", "/api/users")
                .putHeader("content-type","application/json; charset=utf-8")
                .putHeader("content-length", String.valueOf(json.length()))
                .handler(response ->  {
                    testContext.assertEquals(response.statusCode(), 422);
                    testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                       final ValidationError validationError = Json.decodeValue(body.toString(), ValidationError.class);
                       testContext.assertNotNull(validationError);
                       testContext.assertEquals(ValidationError.INVALID_EMAIL_MESSAGE, validationError.getError());
                       async.complete();
                    });
                }).write(json).end();
    }

    /**
     * This test case verifies that an error is thrown by the Registration endpoint, "/api/users" when no value supplied for username
     *
     * @param testContext
     */
    @Test
    public void testRegistrationUsernameValidation(TestContext testContext){
        LOGGER.debug("Testing validation for 'username'");

        final Async async = testContext.async();

        User user = new User("conduituser@vertx.io", null, "conduitpassword", null, null, null, null);
        final String payload = Json.encodePrettily(user);

        LOGGER.debug(payload);

        try{
            vertx.createHttpClient().post(8080, "localhost", "/api/users")
                    .putHeader("content-type","application/json; charset=utf-8")
                    .putHeader("content-length", String.valueOf(payload.length()))
                    .handler(
                            response ->{
                                testContext.assertEquals(422, response.statusCode());
                                testContext.assertEquals("application/json; charset=utf-8", response.getHeader("content-type"));
                                response.bodyHandler(body ->{
                                    final ValidationError validationError = Json.decodeValue(body.toString(), ValidationError.class);
                                    LOGGER.debug(validationError.toString());
                                    testContext.assertNotNull(validationError);
                                    testContext.assertEquals(ValidationError.EMPTY_USERNAME_MESSAGE, validationError.getError());
                                    async.complete();
                                });
                            })
                    .write(payload)
                    .end();

        }catch (Exception e){
            assertNull(e);
        }
    }

    /**
     * This test case verifies that an error is thrown by the Registration endpoint, "/api/users" when no value supplied for password
     *
     * @param testContext
     */
    @Test
    public void testRegistrationPasswordValidation(TestContext testContext){
        LOGGER.debug("Testing validation for 'password");

        final Async async = testContext.async();

        User user = new User("conduituser@vertx.io", "conduitusername", null, null, null, null, null);
        final String payload = Json.encodePrettily(user);

        LOGGER.debug(payload);

        try{
            vertx.createHttpClient().post(8080, "localhost", "/api/users")
                    .putHeader("content-type","application/json; charset=utf-8")
                    .putHeader("content-length", String.valueOf(payload.length()))
                    .handler(
                            response ->{
                                testContext.assertEquals(422, response.statusCode());
                                testContext.assertEquals("application/json; charset=utf-8", response.getHeader("content-type"));
                                response.bodyHandler(body ->{
                                    final ValidationError validationError = Json.decodeValue(body.toString(), ValidationError.class);
                                    LOGGER.debug(validationError.toString());
                                    testContext.assertNotNull(validationError);
                                    testContext.assertEquals(ValidationError.EMPTY_PASSWORD_MESSAGE, validationError.getError());
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
    public void testValidationForEmptyEmailAddress(TestContext testContext){
        User user = new User(" ", "username", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.INVALID_EMAIL_MESSAGE));
    }

    @Test
    public void testValidationForEmptyUsername(TestContext testContext){
        User user = new User("conduituser@vertx.io", "   ", "password");
        testParameter(testContext, user, new ValidationError(ValidationError.EMPTY_USERNAME_MESSAGE));
    }


    void testParameter(TestContext testContext, User user, ValidationError validationError){
        LOGGER.debug("testing for: " + validationError.getError());

        final Async async = testContext.async();

        final String payload = Json.encodePrettily(user);

        LOGGER.debug(payload);

        try{
            vertx.createHttpClient().post(8080, "localhost", "/api/users")
                    .putHeader("content-type","application/json; charset=utf-8")
                    .putHeader("content-length", String.valueOf(payload.length()))
                    .handler(
                            response ->{
                                testContext.assertEquals(422, response.statusCode());
                                testContext.assertEquals("application/json; charset=utf-8", response.getHeader("content-type"));
                                response.bodyHandler(body ->{
                                    final ValidationError validationErrorReturned = Json.decodeValue(body.toString(), ValidationError.class);
                                    LOGGER.debug(validationError.toString());
                                    testContext.assertNotNull(validationError);
                                    testContext.assertEquals(validationError.getError(), validationErrorReturned.getError());
                                    async.complete();
                                });
                            })
                    .write(payload)
                    .end();

        }catch (Exception e){
            assertNull(e);
        }

    }
}
