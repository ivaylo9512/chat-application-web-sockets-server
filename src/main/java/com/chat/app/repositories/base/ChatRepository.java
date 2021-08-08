package com.chat.app.repositories.base;

import com.chat.app.models.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value="FROM Chat as c LEFT JOIN c.firstUserModel as firstUser LEFT JOIN c.secondUserModel as secondUser where first_user = :user AND (lower(concat(secondUser.firstName, ' ', secondUser.lastName)) like lower(concat(:name, '%')) AND secondUser.id > :lastId OR lower(concat(secondUser.firstName, ' ', secondUser.lastName)) > lower(concat(:name, '%'))) or second_user = :user AND (lower(concat(firstUser.firstName, ' ', firstUser.lastName)) like lower(concat(:name, '%')) OR lower(concat(firstUser.firstName, ' ', firstUser.lastName)) > lower(concat(:name, '%')))")
    List<Chat> findUserChats(@Param("user") long id, Pageable pageable);

    @Query(value="from Chat where first_user = :firstUser and second_user = :secondUser or first_user = :secondUser and second_user = :firstUser")
    Chat findUsersChat(@Param("firstUser") long firstUser, @Param("secondUser") long secondUser );
}
