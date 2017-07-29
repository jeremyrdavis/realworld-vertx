package io.vertx.realworld.conduit.domain;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;

public class User {

    private String id;

    private String email;

    private String token;

    private String username;

    private String password;

    private String bio;

    private URI image;

    public User() {
    }

    public User(String email, String username, String password, String bio, URI image, String token, String id) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.id = id;
    }

//    public User(JsonObject jsonObject){
//        this.email = jsonObject.getString("email");
//        this.token = jsonObject.getString("token");
//        this.username = jsonObject.getString("username");
//        this.password = jsonObject.getString("password");
//        this.bio =  jsonObject.getString("bio");
//        this.image = URI.create(jsonObject.getString("image"));
//        this.id = jsonObject.getString("id");
//    }
//
    public String toString(){
        return new ToStringBuilder(this)
                .append("username", username)
                .append("id", id)
                .append("email", email)
                .append("password", password)
                .append("bio", bio)
                .append("image", image)
                .toString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject()
                .put("email", email)
                .put("username", username)
                .put("password", password)
                .put("bio", bio)
                .put("image", image);
        if (id != null && !id.isEmpty()) {
            json.put("_id", id);
        }
        JsonObject user = new JsonObject().put("user", json);
        return user;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
