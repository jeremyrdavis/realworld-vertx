package io.vertx.realworld.conduit.domain;

import io.vertx.core.json.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;

public class ConduitUser {

    private String id;

    private String email;

    private String token;

    private String username;

    private String password;

    private String bio;

    private URI image;

    public ConduitUser() {
    }

    public ConduitUser(String email, String username, String password, String bio, URI image, String token, String id) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.id = id;
    }

    public ConduitUser(JsonObject jsonObject){
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
        return json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
