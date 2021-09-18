package com.chat.app.models.Dtos;

import com.chat.app.models.Message;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageDto {
    private long id;
    private long chatId;
    private long senderId;
    private long receiverId;

    private String message;
    private LocalTime time;
    private LocalDate session;

    public MessageDto() {
    }

    public MessageDto(Message message) {
        this.id = message.getId();
        this.message = message.getMessage();
        this.chatId = message.getSession().getChat().getId();
        this.receiverId = message.getReceiver().getId();
        this.time = message.getTime();
        this.session = message.getSession().getDate();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getSession() {
        return session;
    }

    public void setSession(LocalDate session) {
        this.session = session;
    }
}
