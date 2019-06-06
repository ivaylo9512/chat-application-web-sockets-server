package com.chat.app.services.base;

import com.chat.app.models.UserModel;
import com.chat.app.models.specs.UserSpec;

import java.util.List;

public interface UserService {

    List<UserModel> findAll();

    UserModel findById(int id);

    UserModel register(UserSpec userSpec, String role);

    UserModel changeUserInfo(int loggedUser, UserSpec userSpec);
}
