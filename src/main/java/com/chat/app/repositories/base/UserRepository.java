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

    @Query(value = "select u from UserModel u where lower(concat(firstName, ' ', lastName)) like lower(concat(:lastName, '%')) AND id > :lastId OR lower(concat(firstName, ' ', lastName)) > lower(concat(:lastName, '%')) order by firstName asc, lastName, id")
    Page<UserModel> findNextByUsernameWithRegex(
            @Param("lastName") String lastName,
            @Param("lastId") int lastId,
            Pageable pageable);

    @Query(value = "select u from UserModel u where lower(concat(firstName, ' ', lastName)) like lower(concat(:name, '%'))")
    Page<UserModel> findByUsernameWithRegex(@Param("name") String name, Pageable pageable);

}
