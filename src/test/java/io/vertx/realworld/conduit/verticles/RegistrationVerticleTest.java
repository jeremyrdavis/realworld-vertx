package io.vertx.realworld.conduit.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(VertxUnitRunner.class)
public class RegistrationVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext testContext){
        vertx = Vertx.vertx();
        vertx.deployVerticle(RegistrationVerticle.class.getName(),testContext.asyncAssertSuccess());
//        System.out.println("verticle deployed");
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
//                    assertEquals(body.getString("username"),"conduitusername");
                        async.complete();
                    });

        }catch (Exception e){
            assertNull(e);
        }


    }
}
