package com.chat.app.services.base;

import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.UserSpec;

import java.util.List;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(long id);

    List<UserModel> findByUsernameWithRegex(String username);

    UserModel create(UserModel user);

    void delete(long id, UserDetails loggedUser);

    UserModel changeUserInfo(long loggedUser, UserSpec userSpec);

    UserModel changePassword(NewPasswordSpec changePasswordSpec);
}
