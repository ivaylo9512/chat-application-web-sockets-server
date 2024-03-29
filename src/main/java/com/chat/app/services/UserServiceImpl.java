package com.chat.app.services;

import com.chat.app.exceptions.DisabledUserException;
import com.chat.app.exceptions.EmailExistsException;
import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.services.base.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserModel findById(long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserModel not found."));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User is unavailable.");
        }

        return user;
    }

    @Override
    public UserModel getById(long id){
        return userRepository.getById(id);
    }

    @Override
    public Page<UserModel> findByUsernameWithRegex(long userId, String name, int take, String lastName, long lastId){
        if(lastName != null){
            return userRepository.findNextByUsernameWithRegex(userId, name, lastName, lastId,
                    PageRequest.of(0, take));
        }

        return userRepository.findByUsernameWithRegex(userId, name,
                PageRequest.of(0, take));
    }

    @Override
    public UserModel create(UserModel user) {
        UserModel existingUser = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (existingUser != null) {
            if(existingUser.getUsername().equals(user.getUsername())){
                throw new UsernameExistsException("Username is already taken.");
            }
            throw new EmailExistsException("Email is already taken.");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }

    @Override
    public UserModel save(UserModel user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(long id, UserDetails loggedUser) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserModel not found."));

        if(id != loggedUser.getId() &&
                !AuthorityUtils.authorityListToSet(loggedUser.getAuthorities()).contains("ROLE_ADMIN")){
            throw new UnauthorizedException("You are not allowed to modify the user.");
        }

        userRepository.delete(user);
    }

    @Override
    public void delete(UserModel user) {
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadCredentialsException("Bad credentials."));

        if(!user.isEnabled()){
            throw new DisabledUserException("You must complete the registration. Check your email.");
        }

        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        return new UserDetails(user, authorities);
    }

    @Override
    public UserModel changeUserInfo(UserSpec userSpec, UserDetails loggedUser){
        if(userSpec.getId() != loggedUser.getId() &&
                !loggedUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            throw new UnauthorizedException("Unauthorized");
        }

        UserModel user = userRepository.findById(userSpec.getId())
                .orElseThrow(() -> new EntityNotFoundException("UserModel not found."));

        if(!user.getUsername().equals(userSpec.getUsername()) || !user.getEmail().equals(userSpec.getEmail())){
            UserModel existingUser = userRepository.findByUsernameOrEmail(userSpec.getUsername(), userSpec.getEmail());

            if(existingUser != null){
                if(existingUser.getUsername().equals(userSpec.getUsername())){
                    throw new UsernameExistsException("Username is already taken.");
                }
                throw new EmailExistsException("Email is already taken.");
            }
        }

        user.setUsername(userSpec.getUsername());
        user.setEmail(userSpec.getEmail());
        user.setFirstName(userSpec.getFirstName());
        user.setLastName(userSpec.getLastName());
        user.setAge(userSpec.getAge());
        user.setCountry(userSpec.getCountry());

        return userRepository.save(user);
    }

    @Override
    public UserModel changePassword(NewPasswordSpec passwordSpec, long loggedUser){
        UserModel user = this.findById(loggedUser);

        if (!BCrypt.checkpw(passwordSpec.getCurrentPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid current password.");
        }

        user.setPassword(BCrypt.hashpw(passwordSpec.getNewPassword(),BCrypt.gensalt(4)));
        return userRepository.save(user);
    }

    @Override
    public void setEnabled(boolean state, long id){
        UserModel user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("UserModel not found."));
        user.setEnabled(state);

        userRepository.save(user);
    }
}
