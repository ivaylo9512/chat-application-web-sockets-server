package com.tick42.quicksilver.services;

import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = NullPointerException.class)
    public void findById_withNonExistingUser_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(null);

        userService.findById(1L);
    }

    @Test()
    public void findById_withExistingUser() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.findById(1L);
    }

    @Test(expected = UsernameExistsException.class)
    public void registerUser_WithAlreadyTakenUsername_ShouldThrow() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        when(userRepository.findByUsername("Test")).thenReturn(user);

        userService.create(user);
    }

    @Test()
    public void registerUser() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        when(userRepository.findByUsername("Test")).thenReturn(null);

        UserModel registeredUser = userService.create(user);

        Assert.assertEquals(registeredUser, user);
    }


}

