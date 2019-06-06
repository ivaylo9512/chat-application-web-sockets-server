package com.chat.app.models.DTOs;

import com.chat.app.models.Chat;
import com.chat.app.models.Session;

import java.util.List;

public class ChatDto {
    private int id;
    private UserDto firstUser;
    private UserDto secondUser;
    private List<Session> sessions;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.firstUser = new UserDto(chat.getFirstUserModel());
        this.secondUser = new UserDto(chat.getSecondUserModel());
        this.sessions = chat.getSessions();
    }

    public UserDto getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserDto firstUser) {
        this.firstUser = firstUser;
    }

    public UserDto getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserDto secondUser) {
        this.secondUser = secondUser;
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