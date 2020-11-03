package com.chat.app.services.base;

import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.RegisterSpec;
import com.chat.app.models.specs.UserSpec;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    List<UserModel> findAll();

    UserModel findById(int id);

    List<UserModel> findByUsernameWithRegex(String username);

    UserModel register(RegisterSpec newUser, String role);

    void delete(long id, UserDetails loggedUser);


    UserModel changeUserInfo(int loggedUser, UserSpec userSpec);

    UserModel changePassword(NewPasswordSpec changePasswordSpec);
}
