package models.compositePK;


import models.Chat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class SessionPK implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chat", insertable = false, updatable = false)
    private Chat chat;

    @Column(name = "session_date")
    private LocalDate date;

    public SessionPK() {
    }

    public SessionPK(Chat chat, LocalDate date) {
        this.chat = chat;
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionPK)) return false;
        SessionPK that = (SessionPK) o;
        return Objects.equals(getChat(), that.getChat()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getChat());
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
