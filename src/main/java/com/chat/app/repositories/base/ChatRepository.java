package com.chat.app.repositories.base;

import com.chat.app.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value="SELECT * FROM chats as c LEFT JOIN users firstUser ON c.first_user = firstUser.id LEFT JOIN users secondUser ON c.second_user = secondUser.id " +
            "WHERE c.first_user = :user AND lower(concat(secondUser.firstName, ' ', secondUser.lastName)) " +
            "LIKE lower(concat(:name, '%')) or second_user = :user AND lower(concat(firstUser.firstName, ' ', firstUser.lastName)) " +
            "LIKE lower(concat(:name, '%')) " +
            "ORDER BY IF(second_user = :user, lower(concat(firstUser.firstName, ' ', firstUser.lastName)), lower(concat(secondUser.firstName, ' ', secondUser.lastName))) ASC, c.id", nativeQuery = true)
    Page<Chat> findUserChatsByName(
            @Param("user") long id,
            @Param("name") String name,
            Pageable pageable);

    @Query(value="SELECT * FROM chats as c LEFT JOIN users firstUser ON c.first_user = firstUser.id LEFT JOIN users secondUser ON c.second_user = secondUser.id " +
            "WHERE first_user LIKE :user AND (lower(concat(secondUser.firstName, ' ', secondUser.lastName)) " +
            "LIKE lower(concat(:name, '%')) AND (lower(concat(secondUser.firstName, ' ', secondUser.lastName)) " +
            "LIKE lower(:lastName) AND c.id > :lastId OR lower(concat(secondUser.firstName, ' ', secondUser.lastName)) " +
            "LIKE lower(concat(:name, '%')) AND lower(concat(secondUser.firstName, ' ', secondUser.lastName)) > lower(:lastName))) " +
            "OR second_user = :user AND (lower(concat(firstUser.firstName, ' ', firstUser.lastName)) LIKE lower(concat(:name, '%')) AND (lower(concat(firstUser.firstName, ' ', firstUser.lastName)) " +
            "LIKE lower(:lastName) AND c.id > :lastId OR lower(concat(firstUser.firstName, ' ', firstUser.lastName)) LIKE lower(concat(:name, '%')) AND lower(concat(firstUser.firstName, ' ', firstUser.lastName)) > lower(:lastName))) " +
            "ORDER BY IF(second_user = :user, lower(concat(firstUser.firstName, ' ', firstUser.lastName)), lower(concat(secondUser.firstName, ' ', secondUser.lastName))) ASC, c.id", nativeQuery = true)
    Page<Chat> findNextUserChatsByName(
            @Param("user") long id,
            @Param("name") String name,
            @Param("lastName") String lastName,
            @Param("lastId") long lastId,
            Pageable pageable);

    @Query(value="FROM Chat as c where (first_user = :user or second_user = :user) AND (updated_at = :lastUpdatedAt AND id > :lastId OR updated_at < :lastUpdatedAt) order by updatedAt desc, id asc")
    Page<Chat> findNextUserChats(
            @Param("user") long id,
            @Param("lastId") long lastId,
            @Param("lastUpdatedAt") String lastUpdatedAt,
            Pageable pageable);

    @Query(value="FROM Chat as c where first_user = :user OR second_user = :user order by updatedAt desc, id asc")
    Page<Chat> findUserChats(
            @Param("user") long id,
            Pageable pageable);

    @Query(value="from Chat where first_user = :firstUser and second_user = :secondUser or first_user = :secondUser and second_user = :firstUser")
    Chat findUsersChat(
            @Param("firstUser") long firstUser,
            @Param("secondUser") long secondUser
    );
}
