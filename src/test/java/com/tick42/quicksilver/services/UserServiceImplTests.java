package com.tick42.quicksilver.services;

import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.UserServiceImpl;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
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
        UserModel user = new UserModel("Test", "Test", "ROLE_USER");
        when(userRepository.findByUsername("Test")).thenReturn(null);

        UserModel registeredUser = userService.create(user);

        Assert.assertEquals(registeredUser, user);
    }

    @Test()
    public void registerUser_RoleAdmin() {
        //Arrange
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");

        when(userRepository.findByUsername("Test")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        //Act
        UserModel registeredUser = userService.create(user);

        //Assert
        Assert.assertEquals(registeredUser.getRole(),"ROLE_ADMIN");
    }

    @Test
    public void changePasswords(){
        String password = "currentPassword";
        String newPassword = "newTestPassword1";

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", password, newPassword, newPassword);

        UserModel userModel = new UserModel();
        userModel.setPassword(password);

        when(userRepository.findByUsername("user")).thenReturn(userModel);

        userService.changePassword(passwordSpec);

        Assert.assertEquals(userModel.getPassword(),newPassword);
    }

    @Test(expected = BadCredentialsException.class)
    public void changePasswordState_WithWrongPassword_ShouldThrow(){

        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "InvalidPassword",
                "newTestPassword1","newTestPassword1" );

        UserModel userModel = new UserModel();
        userModel.setPassword("currentPassword");

        when(userRepository.findByUsername("user")).thenReturn(userModel);

        userService.changePassword(passwordSpec);
    }


    @Test(expected = NotFoundException.class)
    public void changeUserInfo_WithNonExistentUser_ShouldThrow(){
        when(userRepository.findById(1L)).thenReturn(null);

        userService.changeUserInfo(1L, new UserSpec());
    }

    @Test()
    public void changeUserInfo(){
        UserSpec user = new UserSpec("username", "firstName", "lastName", 25, "Country");
        UserModel userModel = new UserModel();
        when(userRepository.findById(1L)).thenReturn(Optional.of(any(UserModel.class)));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        userService.changeUserInfo(1L, user);

        Assert.assertThat(user, samePropertyValuesAs(userModel));
    }


}

