package io.vertx.realworld.conduit.domain;

import java.net.URI;

public class User {

    private String email;

    private String token;

    private String username;

    private String password;

    private String bio;

    private URI image;

    public User() {
    }

    public User(String email, String username, String password, String bio, URI image, String token) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
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
