package com.chat.app.repositories.base;

import com.chat.app.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("From Requests where from LIKE :user OR to LIKE :user")
    Request findRequest(@Param("user") long id);

}
