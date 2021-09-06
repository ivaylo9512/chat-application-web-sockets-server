package com.chat.app.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String exception) {
        super(exception);
    }
}
