package com.chat.app.repositories.base;

import com.chat.app.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value="from Message where receiverId = :userId and session_date = :lastCheckDate and time > :lastCheckTime or receiverId = :userId and session_date > :lastCheckDate")
    List<Message> findMostRecentMessages(@Param("userId") long userId, @Param("lastCheckDate") LocalDate lastCheckDate, @Param("lastCheckTime") LocalTime lastCheckTime);
}
