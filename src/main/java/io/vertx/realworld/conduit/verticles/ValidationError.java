package io.vertx.realworld.conduit.verticles;

public class ValidationError {

    private String error;

    public ValidationError(String errorMessage){
        this.error = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
