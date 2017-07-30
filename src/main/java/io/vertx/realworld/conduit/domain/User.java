package io.vertx.realworld.conduit.domain;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;
import java.util.List;

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

    /**
     * Constructor for Login endpoint
     *
     * @param email String
     * @param password String
     */
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }


    /**
     * Constructor with complete arguments
     *
     * @param email String
     * @param username String
     * @param password String
     * @param bio String
     * @param image URI
     * @param token String
     * @param id String
     */
    public User(String email, String username, String password, String bio, URI image, String token, String id) {
        this.email = email;
        this.token = token;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.id = id;
    }

    /**
     * Smaller constructor for use with the Register api endpoint
     *
     * @param email String
     * @param username String
     * @param password String
     */
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(JsonObject jsonObject) {
        if(!StringUtils.isEmpty(StringUtils.trimToEmpty(jsonObject.getString("email")))){
            this.email = jsonObject.getString("email");
        }
        if(!StringUtils.isEmpty(StringUtils.trimToEmpty(jsonObject.getString("username")))){
            this.email = jsonObject.getString("username");
        }
        if(!StringUtils.isEmpty(StringUtils.trimToEmpty(jsonObject.getString("password")))){
            this.email = jsonObject.getString("password");
        }
    }

    /**
     * Null safe implementation
     *
     * @return String
     */
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
