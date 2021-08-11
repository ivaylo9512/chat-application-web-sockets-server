package com.chat.app.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime updatedAt;

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
