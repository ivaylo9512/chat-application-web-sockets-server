package com.chat.app.services.base;

import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.UserSpec;
import org.springframework.data.domain.Page;
import java.util.List;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(long id);

    Page<UserModel> findByUsernameWithRegex(long userId, String username, int take, String lastName, long lastId);

    UserModel create(UserModel user);

    UserModel save(UserModel userModel);

    void delete(long id, UserDetails loggedUser);

    UserModel changeUserInfo(UserSpec userSpec, UserDetails loggedUser);

    UserModel changePassword(NewPasswordSpec changePasswordSpec, long loggedUser);
}
