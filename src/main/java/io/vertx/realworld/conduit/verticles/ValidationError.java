package io.vertx.realworld.conduit.verticles;

public class ValidationError {

    /* Error messages */
    public static final String INVALID_EMAIL_MESSAGE = "invalid email detected";
    public static final String EMPTY_USERNAME_MESSAGE = "empty username detected";
    public static final String EMPTY_PASSWORD_MESSAGE = "empty password detected";

    private String error;

    public ValidationError() {
    }

    public ValidationError(String errorMessage){
        this.error = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error){ this.error = error; }

}
