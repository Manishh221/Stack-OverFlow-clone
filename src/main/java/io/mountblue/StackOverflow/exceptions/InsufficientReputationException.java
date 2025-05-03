package io.mountblue.StackOverflow.exceptions;

public class InsufficientReputationException extends RuntimeException{
    public InsufficientReputationException(String message) {
        super(message);
    }
}
