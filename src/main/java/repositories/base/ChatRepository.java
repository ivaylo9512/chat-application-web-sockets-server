package repositories.base;

import models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query(value="from Chat where first_user = :user or second_user = :user order by id")
    List<Chat> findUserChats(@Param("user") int id);
}
