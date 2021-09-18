package com.chat.app.services;

import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.Request;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.RequestRepository;
import com.chat.app.services.base.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    private Request findById(long id){
        return requestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Request not found."));
    }

    @Override
    public Request findById(long id, long loggedUser){
        Request request = findById(id);

        if(request.getSender().getId() != loggedUser &&
                    request.getReceiver().getId() != loggedUser){
            throw new UnauthorizedException("Unauthorized.");
        }

        return request;
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
    public void delete(Request request) {
        requestRepository.delete(request);
    }

    @Override
    public Request create(UserModel from, UserModel to) {
        return requestRepository.save(new Request(from, to));
    }

    @Override
    public Request verifyAccept(long id, UserDetails loggedUser){
        Request request = this.findById(id);

        if(request.getReceiver().getId() != loggedUser.getId()){
            throw new UnauthorizedException("Unauthorized.");
        }

        return request;
    }

    @Override
    public Request verifyDeny(long id, UserDetails loggedUser){
        Request request = this.findById(id);

        if(request.getReceiver().getId() != loggedUser.getId() &&
                request.getSender().getId() != loggedUser.getId() ){
            throw new UnauthorizedException("Unauthorized.");
        }

        return request;
    }

    @Override
    public Request findRequest(long secondUser, long loggedUser) {
        Request request = requestRepository.findRequest(secondUser, loggedUser);

        if(request == null){
            throw new EntityNotFoundException("Request not found.");
        }

        return request;
    }
}
