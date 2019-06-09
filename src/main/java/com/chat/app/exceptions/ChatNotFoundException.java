package com.chat.app.exceptions;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String exception) {
        super(exception);
    }
}
