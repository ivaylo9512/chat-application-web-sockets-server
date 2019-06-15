package com.chat.app.models.DTOs;

import com.chat.app.models.Chat;
import com.chat.app.models.Session;

import java.util.List;

public class ChatDto {
    private int id;
    private UserDto user;
    private List<Session> sessions;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.sessions = chat.getSessions();
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}