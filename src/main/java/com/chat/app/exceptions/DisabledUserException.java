package com.chat.app.exceptions;

public class DisabledUserException extends RuntimeException{
    public DisabledUserException(String message){
        super(message);
    }
}
