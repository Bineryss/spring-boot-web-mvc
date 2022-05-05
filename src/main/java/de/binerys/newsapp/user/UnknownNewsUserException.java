package de.binerys.newsapp.user;

public class UnknownNewsUserException extends RuntimeException {
    public UnknownNewsUserException(String msg) {
        super(msg);
    }
}
