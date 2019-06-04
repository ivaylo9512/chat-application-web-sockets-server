package com.vision.project.models.DTOs;

import com.vision.project.models.Message;

import java.time.LocalDate;
import java.time.LocalTime;

public class MessageDto {
    private int chatId;
    private int senderId;
    private int receiverId;

    private String message;
    private LocalTime time;
    private LocalDate session;

    public MessageDto() {
    }

    public MessageDto(String message, int chatId, int senderId, int receiverId) {
        this.message = message;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public MessageDto(Message message) {
        this.message = message.getMessage();
        this.chatId = message.getSession().getChat().getId();
        this.receiverId = message.getReceiverId();
        this.time = message.getTime();
        this.session = message.getSession().getDate();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
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
