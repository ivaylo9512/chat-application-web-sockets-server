package com.chat.app.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "first_user", updatable = false)
    private UserModel firstUserModel;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "second_user", updatable = false)
    private UserModel secondUserModel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Session> sessions;

    public Chat() {
    }

    public Chat(UserModel firstUserModel, UserModel secondUserModel) {
        this.firstUserModel = firstUserModel;
        this.secondUserModel = secondUserModel;
    }

    public UserModel getFirstUserModel() {
        return firstUserModel;
    }

    public void setFirstUserModel(UserModel firstUserModel) {
        this.firstUserModel = firstUserModel;
    }

    public UserModel getSecondUserModel() {
        return secondUserModel;
    }

    public void setSecondUserModel(UserModel secondUserModel) {
        this.secondUserModel = secondUserModel;
    }

    public long getId() {
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
