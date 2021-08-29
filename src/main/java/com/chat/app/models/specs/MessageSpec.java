package com.chat.app.models.specs;

import javax.validation.constraints.NotNull;

public class MessageSpec {
    @NotNull
    private long chatId;

    @NotNull
    private long senderId;

    @NotNull
    private long receiverId;

    @NotNull
    private String message;

    public MessageSpec(){

    }

    public MessageSpec(int chatId, int senderId, int receiverId, String message) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
