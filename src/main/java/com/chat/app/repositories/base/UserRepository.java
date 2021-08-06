package com.chat.app.repositories.base;

import com.chat.app.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    List<UserModel> findAll();

    UserModel findByUsername(String username);

    @Query(value = "from UserModel where lower(username) like lower(concat(:username,'%'))")
    List<UserModel> findByUsernameWithRegex(@Param("username") String username);
}
