package models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "first_user", insertable = false, updatable = false)
    private com.vision.project.models.UserModel firstUserModel;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "second_user", insertable = false, updatable = false)
    private com.vision.project.models.UserModel secondUserModel;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Session> sessions;

    public Chat() {
    }

    public Chat(com.vision.project.models.UserModel firstUserModel, com.vision.project.models.UserModel secondUserModel) {
        this.firstUserModel = firstUserModel;
        this.secondUserModel = secondUserModel;
    }

    public com.vision.project.models.UserModel getFirstUserModel() {
        return firstUserModel;
    }

    public void setFirstUserModel(com.vision.project.models.UserModel firstUserModel) {
        this.firstUserModel = firstUserModel;
    }

    public com.vision.project.models.UserModel getSecondUserModel() {
        return secondUserModel;
    }

    public void setSecondUserModel(com.vision.project.models.UserModel secondUserModel) {
        this.secondUserModel = secondUserModel;
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
