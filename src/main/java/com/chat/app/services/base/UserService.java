package com.chat.app.services.base;

import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.UserSpec;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    UserModel findById(long id);

    UserModel getById(long id);

    Page<UserModel> findByUsernameWithRegex(long userId, String username, int take, String lastName, long lastId);

    UserModel create(UserModel user);

    void delete(long id, UserDetails loggedUser);

    UserModel changeUserInfo(UserSpec userSpec, UserDetails loggedUser);

    UserModel changePassword(NewPasswordSpec changePasswordSpec, long loggedUser);

    void setEnabled(boolean state, long id);
}
