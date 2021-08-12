package com.tick42.quicksilver.services;

import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test(expected = EntityNotFoundException.class)
    public void findById_withNonExistingUser_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

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
        when(userRepository.save(user)).thenReturn(user);

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


    @Test(expected = EntityNotFoundException.class)
    public void changeUserInfo_WithNonExistentUser_ShouldThrow(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.changeUserInfo(1L, new UserSpec());
    }

    @Test()
    public void changeUserInfo(){
        UserSpec user = new UserSpec("username", "firstName", "lastName", 25, "Country");
        UserModel userModel = new UserModel();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        userService.changeUserInfo(1L, user);

        Assert.assertEquals(userModel.getFirstName(), user.getFirstName());
        Assert.assertEquals(userModel.getLastName(), user.getLastName());
        Assert.assertEquals(userModel.getCountry(), user.getCountry());
        Assert.assertEquals(userModel.getAge(), user.getAge());
    }

    @Test(expected = BadCredentialsException.class)
    public void loadByUsername_WithNonExistentUsername_shouldThrow(){
        when(userRepository.findByUsername("username")).thenReturn(null);

        userService.loadUserByUsername("username");
    }

    @Test()
    public void loadByUsername(){
        UserModel userModel = new UserModel("username", "password", "ROLE_ADMIN");
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority(userModel.getRole())));

        UserDetails userDetails = new UserDetails(userModel, authorities);

        when(userRepository.findByUsername("username")).thenReturn(userModel);

        UserDetails user = userService.loadUserByUsername("username");
        Assert.assertEquals(userDetails, user);
    }

    @Test(expected = EntityNotFoundException.class)
    public void delete_WithNonExistentUsername_shouldThrow(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        userService.delete(1L, any(UserDetails.class));
    }

    @Test(expected = UnauthorizedException.class)
    public void delete_WithDifferentLoggedId_ThatIsNotAdmin_shouldThrow(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }

    @Test()
    public void delete_WithDifferentLoggedId_ThatIsAdmin(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }

    @Test()
    public void delete_WithSameLoggedId(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }
}

