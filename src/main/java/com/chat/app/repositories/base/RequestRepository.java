package com.chat.app.repositories.base;

import com.chat.app.models.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("From Request as r where (r.from LIKE :firstUser AND to LIKE :secondUser) " +
            "OR (r.from LIKE :secondUser AND to LIKE :firstUser)")
    Request findRequest(@Param("firstUser") long firstUser, @Param("secondUser") long secondUser);

    @Query("Select r From Request r where to LIKE :user ORDER BY createdAt desc, id asc")
    Page<Request> findAll(
            @Param("user") long user,
            Pageable pageable);

    @Query("Select r From Request r where to LIKE :user AND (createdAt LIKE :lastCreatedAt AND id > :lastId OR createdAt > :lastCreatedAt) " +
            "ORDER BY createdAt desc, id asc")
    Page<Request> findNextAll(
            @Param("user") long user,
            @Param("lastCreatedAt") String lastCreatedAt,
            @Param("lastId") long lastId,
            Pageable pageable);
}
