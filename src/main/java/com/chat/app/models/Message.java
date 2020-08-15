package com.chat.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "messages")
public class Message{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver")
    private UserModel receiver;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "message")
    private String message;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "chat"),@JoinColumn(name = "session_date")})
    private Session session;

    public Message(UserModel receiver, LocalTime time, String message, Session session){
        this.receiver = receiver;
        this.time = time;
        this.message = message;
        this.session = session;
    }
    public Message() {
    }

    public UserModel getReceiver() {
        return receiver;
    }

    public void setReceiver(UserModel receiverId) {
        this.receiver = receiverId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
