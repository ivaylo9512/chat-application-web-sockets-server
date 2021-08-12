package com.chat.app.services.base;

import com.chat.app.models.Request;
import org.springframework.data.domain.Page;

public interface RequestService {

    Page<Request> findAll(Long userId, int pageSize, String lastCreatedAt, long lastId);

    Request findByUsers(long firstUser, long secondUser);

    void delete(long id);

    Request create(long from, long to);
}