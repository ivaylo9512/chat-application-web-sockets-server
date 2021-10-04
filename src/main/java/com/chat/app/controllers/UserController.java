package com.chat.app.controllers;

import com.chat.app.exceptions.EmailExistsException;
import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.exceptions.UsernameExistsException;
import com.chat.app.models.*;
import com.chat.app.models.Dtos.PageDto;
import com.chat.app.models.Dtos.UserDto;
import com.chat.app.models.specs.NewPasswordSpec;
import com.chat.app.models.specs.RegisterSpec;
import com.chat.app.models.specs.UserSpec;
import com.chat.app.services.base.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;
    private final ChatService chatService;
    private final FileService fileService;
    private final RequestService requestService;
    private final EmailTokenService emailTokenService;

    public UserController(UserService userService, ChatService chatService, FileService fileService, RequestService requestService, EmailTokenService emailTokenService) {
        this.userService = userService;
        this.chatService = chatService;
        this.fileService = fileService;
        this.requestService = requestService;
        this.emailTokenService = emailTokenService;
    }

    @PostMapping(value = "/register")
    public void register(@Valid @ModelAttribute RegisterSpec registerSpec) throws IOException, MessagingException {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"profileImage", "image");
        }

        UserModel newUser = userService.create(new UserModel(registerSpec, file, "ROLE_USER"));

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        emailTokenService.sendVerificationEmail(newUser);
    }

    @GetMapping(value = "/activate/{token}")
    public void activate(@PathVariable("token") String token, HttpServletResponse httpServletResponse) throws IOException {
        EmailToken emailToken = emailTokenService.getToken(token);
        UserModel user = emailToken.getUser();

        if(emailToken.getExpiryDate().isBefore(LocalDateTime.now())){
            emailTokenService.delete(emailToken);
            userService.delete(emailToken.getUser());

            throw new UnauthorizedException("Token has expired. Repeat your registration.");
        }

        user.setEnabled(true);

        userService.save(user);
        emailTokenService.delete(emailToken);

        httpServletResponse.sendRedirect("https://localhost:4200");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/auth/registerAdmin")
    public UserDto registerAdmin(@Valid @ModelAttribute RegisterSpec registerSpec) throws IOException {
        MultipartFile profileImage = registerSpec.getProfileImage();
        File file = null;

        if(profileImage != null){
            file = fileService.generate(profileImage,"profileImage", "image/png");
        }

        UserModel newUser = userService.create(new UserModel(registerSpec, file, "ROLE_ADMIN"));
        newUser.setEnabled(true);

        if(file != null){
            fileService.save(file.getResourceType() + newUser.getId(), registerSpec.getProfileImage());
        }

        return new UserDto(newUser);
    }

    @PostMapping("/login")
    public UserDto login(){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return new UserDto(loggedUser.getUserModel());
    }

    @GetMapping(value = "/findById/{id}")
    public UserDto findById(@PathVariable(name = "id") long id){
        return new UserDto(userService.findById(id));
    }

    @GetMapping(value = {"/auth/searchForUsers/{take}/{name}", "/auth/searchForUsers/{take}/{lastName}/{lastId}",
            "/auth/searchForUsers/{take}", "/auth/searchForUsers/{take}/{name}/{lastName}/{lastId}"})
    public PageDto<UserDto> searchForUsers(
            @PathVariable(name = "take") int take,
            @PathVariable(name = "name", required = false) String name,
            @PathVariable(name = "lastName", required = false) String lastName,
            @PathVariable(name = "lastId", required = false) Long lastId
    ){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Page<UserModel> page = userService.findByUsernameWithRegex(loggedUser.getId(), name == null ? "" : name, take, lastName, lastId == null ? 0 : lastId);

        List<UserDto> users = page.getContent().stream().map(userModel -> {
            Chat chat = chatService.findUsersChat(userModel.getId(), loggedUser.getId());
            Request request = requestService.findByUsers(userModel.getId(), loggedUser.getId());

            if(chat == null) {
                return new UserDto(userModel, request);
            }

            return new UserDto(userModel, chat);
        }).collect(Collectors.toList());

        return new PageDto<>(page.getTotalElements(), users);
    }

    @PatchMapping(value = "/auth/changeUserInfo")
    public UserDto changeUserInfo(@Valid @RequestBody UserSpec userModel){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changeUserInfo(userModel, loggedUser));
    }

    @PatchMapping(value = "/auth/changePassword")
    public UserDto changePassword(@Valid @RequestBody NewPasswordSpec newPasswordSpec){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return new UserDto(userService.changePassword(newPasswordSpec, loggedUser.getId()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/auth/setEnabled/{state}/{id}")
    public void setEnable(@PathVariable(name = "state") boolean state,
                                 @PathVariable(name = "id") long id){
        userService.setEnabled(state, id);
    }

    @ExceptionHandler
    ResponseEntity<String> handleUsernameExistsException(UsernameExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleEmailExistsException(EmailExistsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
