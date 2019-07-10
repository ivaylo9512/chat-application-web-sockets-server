package com.chat.app.models.specs;

public class MessageSpec {

    String message;
    String username;
    int chatId;

    MessageSpec(){

    }
    MessageSpec(String username, String message, int chatId){
        this.username = username;
        this.message = message;
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
