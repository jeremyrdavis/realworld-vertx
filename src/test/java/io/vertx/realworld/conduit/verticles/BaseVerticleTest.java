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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertNull;

@RunWith(VertxUnitRunner.class)
public abstract class BaseVerticleTest{

    protected static final Logger LOGGER = LoggerFactory.getLogger(UsersRegistrationVerticleTest.class);
    protected static MongodProcess MONGO;
    protected static int MONGO_PORT = 12345;
    protected static int HTTP_PORT = 8080;
    protected Vertx vertx;
    protected String endpoint;


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
                .net(new Net("localhost",MONGO_PORT, Network.localhostIsIPv6()))
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
                        .put("connection_string", "mongodb://localhost:" + MONGO_PORT)
                        .put("ssl", "false"));
        vertx = Vertx.vertx();
        vertx.deployVerticle(UsersVerticle.class.getName(), options, testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext testContext){
        vertx.close(testContext.asyncAssertSuccess());
    }

    protected void testParameter(TestContext testContext, User user, ValidationError validationError){
        LOGGER.debug("testing for: " + validationError.getError());

        final Async async = testContext.async();

        final String payload = Json.encodePrettily(user);

        LOGGER.debug(payload);

        try{
            vertx.createHttpClient()
                    .post(8080, "localhost", endpoint)
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
