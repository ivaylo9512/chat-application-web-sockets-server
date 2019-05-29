package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.compositePK.SessionPK;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@IdClass(SessionPK.class)
@Table(name = "sessions")
public class Session {
    @Id
    @JsonIgnore
    private Chat chat;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "session", cascade = CascadeType.ALL)
    private List<Message> messages;
    public Session() {
    }

    public Session(Chat chat, LocalDate date) {
        this.date = date;
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
