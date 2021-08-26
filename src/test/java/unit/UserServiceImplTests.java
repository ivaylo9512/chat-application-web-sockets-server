package unit;

import com.chat.app.exceptions.PasswordsMissMatchException;
import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.RegisterSpec;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.repositories.base.UserRepository;
import com.chat.app.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void findById_withNonExistingUser_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findById(1L)
        );

        assertEquals(thrown.getMessage(), "User not found.");
    }

    @Test()
    public void findById_withExistingUser() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.findById(1L);
    }

    @Test
    public void registerUser_WithAlreadyTakenUsername_ShouldThrow() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        when(userRepository.findByUsername("Test")).thenReturn(user);

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.create(user)
        );

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }

    @Test
    public void registerUser() {
        UserModel user = new UserModel("Test", "Test", "ROLE_USER");

        when(userRepository.findByUsername("Test")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        UserModel registeredUser = userService.create(user);

        assertEquals(registeredUser, user);
    }

    @Test
    public void registerUser_RoleAdmin() {
        //Arrange
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");

        when(userRepository.findByUsername("Test")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        //Act
        UserModel registeredUser = userService.create(user);

        //Assert
        assertEquals(registeredUser.getRole(),"ROLE_ADMIN");
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

        assertEquals(userModel.getPassword(),newPassword);
    }

    @Test
    public void changePasswordState_WithWrongPassword_ShouldThrow(){
        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "InvalidPassword",
                "newTestPassword1","newTestPassword1" );

        UserModel userModel = new UserModel();
        userModel.setPassword("currentPassword");

        when(userRepository.findByUsername("user")).thenReturn(userModel);

        BadCredentialsException thrown = assertThrows(
                BadCredentialsException.class,
                () -> userService.changePassword(passwordSpec)
        );

        assertEquals(thrown.getMessage(), "Invalid current password.");
    }

    @Test
    public void ChangePasswordState_WithNotMatchingPasswords_ShouldThrow(){
        String name = "name";

        NewPasswordSpec passwordSpec = new NewPasswordSpec();
        passwordSpec.setUsername(name);
        passwordSpec.setCurrentPassword("Current");
        passwordSpec.setNewPassword("newTestPassword1");
        passwordSpec.setRepeatNewPassword("InvalidPassword");

        PasswordsMissMatchException thrown = assertThrows(
                PasswordsMissMatchException.class,
                () -> userService.changePassword(passwordSpec)
        );

        assertEquals(thrown.getMessage(), "Passwords don't match");
    }

    @Test
    public void RegisterUser_WithNotMatchingPasswords_shouldThrow() {
        RegisterSpec newRegistration = new RegisterSpec("Test", "TestPassword", "TestPasswordMissMatch");

        UserModel userModel = new UserModel(newRegistration, "ROLE_USER");

        when(userRepository.findByUsername("Test")).thenReturn(null);

        PasswordsMissMatchException thrown = assertThrows(
                PasswordsMissMatchException.class,
                () -> userService.create(userModel)
        );

        assertEquals(thrown.getMessage(), "Password doesn't match.");
    }

    @Test()
    public void changeUserInfo_WithNonExistentUser_ShouldThrow(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changeUserInfo(1L, new UserSpec())
        );

        assertEquals(thrown.getMessage(), "Username not found.");
    }

    @Test()
    public void changeUserInfo(){
        UserSpec user = new UserSpec("username", "firstName", "lastName", 25, "Country");
        UserModel userModel = new UserModel();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        userService.changeUserInfo(1L, user);

        assertEquals(userModel.getFirstName(), user.getFirstName());
        assertEquals(userModel.getLastName(), user.getLastName());
        assertEquals(userModel.getCountry(), user.getCountry());
        assertEquals(userModel.getAge(), user.getAge());
    }

    @Test
    public void loadByUsername_WithNonExistentUsername_shouldThrow(){
        when(userRepository.findByUsername("username")).thenReturn(null);

        BadCredentialsException thrown = assertThrows(
                BadCredentialsException.class,
                () -> userService.loadUserByUsername("username")
        );

        assertEquals(thrown.getMessage(), "Bad credentials");
    }

    @Test
    public void loadByUsername(){
        UserModel userModel = new UserModel("username", "password", "ROLE_ADMIN");
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(userModel.getRole()));

        UserDetails userDetails = new UserDetails(userModel, authorities);

        when(userRepository.findByUsername("username")).thenReturn(userModel);

        UserDetails user = userService.loadUserByUsername("username");
        assertEquals(userDetails, user);
    }

    @Test()
    public void delete_WithNonExistentUsername_shouldThrow(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.delete(1L, any(UserDetails.class))
        );

        assertEquals(thrown.getMessage(), "User not found.");
    }

    @Test()
    public void delete_WithDifferentLoggedId_ThatIsNotAdmin_shouldThrow(){
        List<SimpleGrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        UnauthorizedException thrown = assertThrows(
                UnauthorizedException.class,
                () -> userService.delete(1L, userDetails)
        );

        assertEquals(thrown.getMessage(), "You are not allowed to modify the user.");
    }

    @Test
    public void delete_WithDifferentLoggedId_ThatIsAdmin(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }

    @Test
    public void delete_WithSameLoggedId(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }
}

