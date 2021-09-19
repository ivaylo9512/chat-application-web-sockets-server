package com.chat.app.models.Dtos;

import com.chat.app.models.Chat;
import com.chat.app.models.Session;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDto {
    private long id;
    private UserDto firstUser;
    private UserDto secondUser;
    private String updatedAt;
    private String createdAt;
    private List<SessionDto> sessions;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.firstUser = new UserDto(chat.getFirstUser());
        this.secondUser = new UserDto(chat.getSecondUser());
        this.updatedAt = chat.getUpdatedAt().toString();
        this.createdAt = chat.getCreatedAt().toString();
        toSessionsDto(chat.getSessions());
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<SessionDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDto> sessions) {
        if(sessions != null){
            this.sessions = sessions;
        }
    }

    public void toSessionsDto(List<Session> sessions) {
        if(sessions != null){
            this.sessions = sessions.stream().map(SessionDto::new).collect(Collectors.toList());
        }
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}