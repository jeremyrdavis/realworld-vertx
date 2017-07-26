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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(VertxUnitRunner.class)
public class RegistrationVerticleTest {

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
        System.out.println("verticle deployed");
    }

    @After
    public void tearDown(TestContext testContext){
        vertx.close(testContext.asyncAssertSuccess());
    }

    /**
     * This tests the implementation of the Registration endpoint
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
     *       "bio": "I work at statefarm",
     *       "image": null
     *       }
     *   }
     * @param testContext
     * @see "https://github.com/gothinkster/realworld/tree/master/api"
     */
    @Test
    public void testRegisterNewUser(TestContext testContext){
        System.out.println("testRegisterNewUser");

        final Async async = testContext.async();

        WebClient client = WebClient.create(vertx);
        System.out.println("client created");

        try{
            client.post(8080, "localhost", "/api/users").sendJsonObject(
                    new JsonObject().put("username", "conduitusername")
                            .put("email", "conduituser@vertx.io")
                            .put("password", "conduituserpassword"),
                    ar ->{
                        testContext.assertTrue(ar.succeeded());

                        HttpResponse<Buffer> response = ar.result();

                        testContext.assertEquals(response.statusCode(), 201);
                        testContext.assertEquals(response.getHeader("content-type"), "application/json; charset=utf-8");

                        JsonObject body = response.bodyAsJsonObject();
                        System.out.println(body);
                        assertNotNull(body);
                        async.complete();
                    });

        }catch (Exception e){
            assertNull(e);
        }
    }


    @Test
    public void testRegisterNewUserValidationForEmail(TestContext testContext){
        System.out.println("testRegisterNewUser");

        final Async async = testContext.async();

        WebClient client = WebClient.create(vertx);
        System.out.println("client created");

        try{
            client.post(8080, "localhost", "/api/users").sendJsonObject(
                    new JsonObject().put("username", "conduitusername")
                            .put("password", "conduituserpassword"),
                    ar ->{
                        testContext.assertTrue(ar.succeeded());

                        HttpResponse<Buffer> response = ar.result();

                        testContext.assertEquals(response.statusCode(), 422);
                        testContext.assertEquals(response.getHeader("content-type"), "application/json; charset=utf-8");

                        JsonObject body = response.bodyAsJsonObject();
                        System.out.println(body);
                        assertNotNull(body);
                        async.complete();
                    });

        }catch (Exception e){
            assertNull(e);
        }
    }

}
