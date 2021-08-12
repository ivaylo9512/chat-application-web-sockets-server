package com.chat.app.repositories.base;

import com.chat.app.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    List<UserModel> findAll();

    UserModel findByUsername(String username);

    @Query(value = "SELECT u from UserModel u " +
            "WHERE lower(concat(firstName, ' ', lastName)) like lower(concat(:name, '%')) AND id != :userId " +
            "AND (lower(concat(firstName, ' ', lastName)) like lower(concat(:lastName, '%')) AND id > :lastId OR lower(concat(firstName, ' ', lastName)) > lower(concat(:lastName, '%'))) " +
            "ORDER BY firstName ASC, lastName ASC, id ASC")
    Page<UserModel> findNextByUsernameWithRegex(
            @Param("userId") long userId,
            @Param("name") String name,
            @Param("lastName") String lastName,
            @Param("lastId") long lastId,
            Pageable pageable);

    @Query(value = "SELECT u from UserModel u " +
            "WHERE lower(concat(firstName, ' ', lastName)) LIKE lower(concat(:name, '%')) AND id != :userId " +
            "ORDER BY firstName ASC, lastName ASC, id ASC")
    Page<UserModel> findByUsernameWithRegex(
            @Param("userId") long userId,
            @Param("name") String name,
            Pageable pageable);
}
