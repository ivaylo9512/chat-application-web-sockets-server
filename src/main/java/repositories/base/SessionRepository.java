package repositories.base;

import models.Chat;
import models.Session;
import models.compositePK.SessionPK;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, SessionPK> {
    @Query(value="from Session where chat = :chat")
    List<Session> getSessions(@Param("chat") Chat chat, Pageable pageable);
}
