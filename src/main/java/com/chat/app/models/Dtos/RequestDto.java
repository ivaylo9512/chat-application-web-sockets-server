package com.chat.app.models.Dtos;

import com.chat.app.models.Request;

public class RequestDto {
    private long id;
    private UserDto sender;
    private String createdAt;

    public RequestDto() {
    }

    public RequestDto(Request request) {
        this.id = request.getId();
        this.sender = new UserDto(request.getSender());
        this.createdAt = request.getCreatedAt().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserDto getSender() {
        return sender;
    }

    public void setSender(UserDto sender) {
        this.sender = sender;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}