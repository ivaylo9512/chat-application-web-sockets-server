package unit;

import com.chat.app.exceptions.DisabledUserException;
import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
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

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void findById_withExistingUser() {
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        user.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.findById(1L);
    }

    @Test()
    public void findById_withNotEnabledUser(){
        UserModel user = new UserModel("Test", "Test", "ROLE_ADMIN");
        user.setEnabled(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        DisabledUserException thrown = assertThrows(DisabledUserException.class,
                () -> userService.findById(1));

        assertEquals(thrown.getMessage(), "You must complete the registration. Check your email.");
    }

    @Test
    public void registerUser_WithAlreadyTakenUsername_UsernameExists() {
        UserModel existingUser = new UserModel("test", "test@gmail.com", "test", "ROLE_ADMIN");
        UserModel user = new UserModel("test", "nonexistent@gmail.com", "test", "ROLE_ADMIN");

        when(userRepository.findByUsernameOrEmail("test", "nonexistent@gmail.com")).thenReturn(existingUser);

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.create(user)
        );

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }

    @Test
    public void registerUser() {
        UserModel user = new UserModel("test", "test@gmail.com", "test", "ROLE_ADMIN");

        when(userRepository.findByUsernameOrEmail("test", "test@gmail.com")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        UserModel registeredUser = userService.create(user);

        assertEquals(registeredUser, user);
    }

    @Test
    public void registerUser_RoleAdmin() {
        UserModel user = new UserModel("test", "test@gmail.com", "test", "ROLE_ADMIN");

        when(userRepository.findByUsernameOrEmail("test", "test@gmail.com")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        UserModel registeredUser = userService.create(user);

        assertEquals(registeredUser.getRole(),"ROLE_ADMIN");
    }

    @Test
    public void changePasswords(){
        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "currentPassword", "newTestPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword(BCrypt.hashpw(passwordSpec.getCurrentPassword(),BCrypt.gensalt(4)));
        userModel.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));
        when(userRepository.save(userModel)).thenReturn(userModel);

        UserModel user = userService.changePassword(passwordSpec, 1);
        assertTrue(BCrypt.checkpw("newTestPassword", user.getPassword()));
    }

    @Test
    public void changePasswordState_WithWrongPassword_ShouldThrow(){
        NewPasswordSpec passwordSpec = new NewPasswordSpec("user", "InvalidPassword","newTestPassword" );

        UserModel userModel = new UserModel();
        userModel.setPassword(BCrypt.hashpw("currentPassword",BCrypt.gensalt(4)));
        userModel.setEnabled(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userModel));

        BadCredentialsException thrown = assertThrows(
                BadCredentialsException.class,
                () -> userService.changePassword(passwordSpec, 1)
        );

        assertEquals(thrown.getMessage(), "Invalid current password.");
    }

    @Test
    public void ChangePasswordState_WithNonExistentUser_EntityNotFound(){
        NewPasswordSpec passwordSpec = new NewPasswordSpec("username",
                "current", "newTestPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changePassword(passwordSpec, 1)
        );

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void changeUserInfo_WithNonExistentUser_ShouldThrow(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserSpec userSpec = new UserSpec();
        userSpec.setId(1);

        UserModel loggedUserModel = new UserModel(1, "username",
                "password", "ROLE_ADMIN");
        UserDetails loggedUser = new UserDetails(loggedUserModel, List.of(
                new SimpleGrantedAuthority(loggedUserModel.getRole())));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changeUserInfo(userSpec, loggedUser)
        );

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void changeUserInfo_WhenUserHasDifferentIdAndRoleAdmin(){
        UserSpec newUser = new UserSpec(1, "newUsername", "newUsername@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel();
        oldUser.setUsername("username");

        UserModel loggedUserModel = new UserModel(2, "username",
                "password", "ROLE_ADMIN");
        UserDetails loggedUser = new UserDetails(loggedUserModel, List.of(
                new SimpleGrantedAuthority(loggedUserModel.getRole())));

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(userRepository.findByUsernameOrEmail("newUsername", "newUsername@gmail.com")).thenReturn(null);

        userService.changeUserInfo(newUser, loggedUser);

        assertEquals(oldUser.getUsername(), newUser.getUsername());
        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());
    }

    @Test()
    public void changeUserInfo(){
        UserSpec newUser = new UserSpec(1, "newUsername", "nonexistent@gmail.com", "firstName", "lastName", 25, "Country");

        UserModel oldUser = new UserModel("username", "username@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        UserDetails loggedUser = new UserDetails(oldUser, List.of(
                new SimpleGrantedAuthority(oldUser.getRole())));

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        when(userRepository.findByUsernameOrEmail("newUsername", "nonexistent@gmail.com")).thenReturn(null);

        userService.changeUserInfo(newUser, loggedUser);

        assertEquals(oldUser.getUsername(), newUser.getUsername());
        assertEquals(oldUser.getEmail(), newUser.getEmail());
        assertEquals(oldUser.getFirstName(), newUser.getFirstName());
        assertEquals(oldUser.getLastName(), newUser.getLastName());
        assertEquals(oldUser.getCountry(), newUser.getCountry());
        assertEquals(oldUser.getAge(), newUser.getAge());
    }

    @Test()
    public void changeUserInfo_WhenUsernameIsTaken(){
        UserSpec newUser = new UserSpec(1, "username", "nonexistent@gmail.com", "firstName",
                "lastName", 25, "Country");

        UserModel oldUser = new UserModel("oldUsername", "username@gmail.com", "password", "ROLE_USER");
        oldUser.setId(1);

        UserModel existingUser = new UserModel();
        existingUser.setId(2);
        existingUser.setUsername("username");

        UserDetails loggedUser = new UserDetails(oldUser, List.of(
                new SimpleGrantedAuthority(oldUser.getRole())));

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.findByUsernameOrEmail("username", "nonexistent@gmail.com")).thenReturn(existingUser);

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.changeUserInfo(newUser, loggedUser)
        );

        assertEquals(thrown.getMessage(), "Username is already taken.");
    }

    @Test
    public void loadByUsername_WithNonExistentUsername_BadCredentials(){
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

        UserDetails userDetails = new UserDetails(userModel, List.of(
                new SimpleGrantedAuthority(userModel.getRole())));

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

        assertEquals(thrown.getMessage(), "UserModel not found.");
    }

    @Test()
    public void delete_WithDifferentLoggedId_ThatIsNotAdmin_shouldThrow(){
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"));
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
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }

    @Test
    public void delete_WithSameLoggedId(){
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new UserDetails("username", "password", authorities, 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UserModel()));

        userService.delete(1L, userDetails);
    }
}

