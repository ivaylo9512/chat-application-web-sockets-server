package com.chat.app.services;

import com.chat.app.models.Request;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.RequestRepository;
import com.chat.app.services.base.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

@Service
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;

    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Page<Request> findAll(Long userId, int pageSize, String lastCreatedAt, long lastId) {
        if(lastCreatedAt == null){
            return requestRepository.findAll(userId, PageRequest.of(0, pageSize));
        }
        return requestRepository.findNextAll(userId, lastCreatedAt, lastId, PageRequest.of(0, pageSize));
    }

    @Override
    public Request findByUsers(long firstUser, long secondUser) {
        return requestRepository.findRequest(firstUser, secondUser);
    }

    @Override
    public void delete(long id) {
        Request request = requestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Request not found."));

        requestRepository.delete(request);
    }

    @Override
    public Request create(UserModel from, UserModel to) {
        return requestRepository.save(new Request(from, to));
    }
}
