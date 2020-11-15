package com.chat.app.models.Dtos;

import com.chat.app.models.Chat;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDto {
    private int id;
    private UserDto firstUser;
    private UserDto secondUser;
    private List<SessionDto> sessions;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.firstUser = new UserDto(chat.getFirstUserModel());
        this.secondUser = new UserDto(chat.getSecondUserModel());
        this.sessions = chat.getSessions().stream().map(SessionDto::new).collect(Collectors.toList());
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

    public List<SessionDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDto> sessions) {
        this.sessions = sessions;
    }
}