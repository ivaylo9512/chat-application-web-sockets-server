package unit;

import com.chat.app.controllers.UserController;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.File;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.RegisterSpec;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.services.FileServiceImpl;
import com.chat.app.services.UserServiceImpl;
import com.chat.app.services.base.EmailTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private EmailTokenService emailTokenService;

    @InjectMocks
    private UserController userController;

    private final MockMultipartFile multipartFile = new MockMultipartFile("imageTest", "imageTest.png", "image/png", "imageTest".getBytes());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria");
    private final File profileImage = new File("profileImage", 32_000, "image/png", "png", userModel);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());

    private void assertUser(UserDto userDto, UserModel userModel) {
        assertEquals(userDto.getId(), userModel.getId());
        assertEquals(userDto.getUsername(), userModel.getUsername());
        assertEquals(userDto.getEmail(), userModel.getEmail());
        assertEquals(userDto.getRole(), userModel.getRole());
        assertEquals(userDto.getCountry(), userModel.getCountry());
        assertEquals(userDto.getFirstName(), userModel.getFirstName());
        assertEquals(userDto.getLastName(), userModel.getLastName());
        assertEquals(userDto.getAge(), userModel.getAge());
    }

    @Test
    public void login(){
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserDto loggedUser = userController.login();

        assertUser(loggedUser, userModel);
    }

    @Test
    public void register() throws IOException, MessagingException {
        RegisterSpec register = new RegisterSpec("username", "email@gmail.com", "password", multipartFile, "firstName", "lastName", "Bulgaria", 25);

        userModel.setProfileImage(profileImage);
        userModel.setRole("ROLE_USER");

        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        when(userService.create(any(UserModel.class))).thenReturn(userModel);
        when(fileService.generate(multipartFile, "profileImage", "image")).thenReturn(profileImage);
        doNothing().when(emailTokenService).sendVerificationEmail(userModel);

        userController.register(register);

        verify(fileService, times(1)).save("profileImage1", multipartFile);
        verify(emailTokenService, times(1)).sendVerificationEmail(userModel);
        verify(userService).create(captor.capture());
        UserModel passedToCreate = captor.getValue();

        assertEquals(passedToCreate.getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getProfileImage(), profileImage);
        assertEquals(passedToCreate.getProfileImage().getOwner().getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getRole(), userModel.getRole());
    }

    @Test
    public void registerAdmin() throws IOException {
        RegisterSpec register = new RegisterSpec("username", "password", "email@gmail.com", multipartFile, "firstName", "lastName", "Bulgaria", 25);

        userModel.setProfileImage(profileImage);
        userModel.setRole("ROLE_ADMIN");

        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        when(userService.create(any(UserModel.class))).thenReturn(userModel);
        when(fileService.generate(multipartFile, "profileImage", "image/png")).thenReturn(profileImage);

        UserDto registeredUser = userController.registerAdmin(register);

        assertUser(registeredUser, userModel);

        verify(fileService, times(1)).save("profileImage1", multipartFile);
        verify(userService).create(captor.capture());
        UserModel passedToCreate = captor.getValue();

        assertEquals(passedToCreate.getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getProfileImage(), profileImage);
        assertEquals(passedToCreate.getProfileImage().getOwner().getUsername(), userModel.getUsername());
        assertEquals(passedToCreate.getRole(), userModel.getRole());
    }

    @Test
    public void findById(){
        when(userService.findById(1L)).thenReturn(userModel);

        UserDto user = userController.findById(1L);

        assertUser(user, userModel);
        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void changeUserInfo(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserSpec userSpec = new UserSpec();

        when(userService.changeUserInfo(userSpec, user)).thenReturn(userModel);

        UserDto userDto = userController.changeUserInfo(userSpec);

        assertUser(userDto, userModel);
    }

    @Test
    public void setEnabled(){
        userController.setEnable(true, 1L);
        verify(userService, times(1)).setEnabled(true, 1L);
    }

    @Test
    public void changePassword(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
        NewPasswordSpec passwordSpec = new NewPasswordSpec("username", "password", "newPassword");

        when(userService.changePassword(passwordSpec, user.getId())).thenReturn(userModel);

        UserDto userDto = userController.changePassword(passwordSpec);

        assertUser(userDto, userModel);
    }
}
