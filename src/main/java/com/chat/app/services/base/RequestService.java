package com.chat.app.services.base;

import com.chat.app.models.Request;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import org.springframework.data.domain.Page;

public interface RequestService {

    Request findById(long id);

    Page<Request> findAll(Long userId, int pageSize, String lastCreatedAt, long lastId);

    Request findByUsers(long firstUser, long secondUser);

    void deleteById(long id);

    void delete(Request request);

    Request create(UserModel from, UserModel to);

    Request verifyAccept(long id, UserDetails loggedUser);

    Request verifyDeny(long id, UserDetails loggedUser);
}
