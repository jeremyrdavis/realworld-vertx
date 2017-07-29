package io.vertx.realworld.conduit.domain;

import org.junit.Test;

public class UserJsonTest {

    /**
     * This test is for the specified format for the Register User endpoint
     * {
     *      "user": {
     *          "email": "jake@jake.jake",
     *          "token": "jwt.token.here",
     *          "username": "jake",
     *          "bio": "I work at statefarm",
     *          "image": null
     *          }
     *  }
     */
    @Test
    public void testJson(){
        User user = new User("conduituser@vertx.io", "conduituser", "conduitpassword", null, null, null, null);
        System.out.println(user.toJson());
    }
}
