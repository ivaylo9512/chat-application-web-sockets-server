package com.chat.app.repositories.base;

import com.chat.app.models.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value="from Chat where first_user = :user or second_user = :user order by id")
    List<Chat> findUserChats(@Param("user") long id, Pageable pageable);

    @Query(value="from Chat where first_user = :firstUser and second_user = :secondUser or first_user = :secondUser and second_user = :firstUser")
    Chat findUsersChat(@Param("firstUser") long firstUser, @Param("secondUser") long secondUser );
}
