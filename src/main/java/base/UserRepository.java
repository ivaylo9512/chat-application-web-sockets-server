package base;

import models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    List<UserModel> findAll();

    UserModel findByUsername(String username);
}
