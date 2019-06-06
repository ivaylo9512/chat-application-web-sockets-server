package com.chat.app.exceptions;

public class PasswordsMissMatchException extends RuntimeException {

    public PasswordsMissMatchException(String exception) {
        super(exception);
    }

}
