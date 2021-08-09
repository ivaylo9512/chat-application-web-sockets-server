package com.chat.app.models.Dtos;

import com.chat.app.models.Chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDto {
    private long id;
    private UserDto firstUser;
    private UserDto secondUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SessionDto> sessions;

    public ChatDto() {
    }

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.firstUser = new UserDto(chat.getFirstUserModel());
        this.secondUser = new UserDto(chat.getSecondUserModel());
        this.sessions = chat.getSessions().stream().map(SessionDto::new).collect(Collectors.toList());
        this.createdAt = chat.getCreatedAt();
        this.updatedAt = chat.getUpdatedAt();
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
        this.sessions = sessions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}