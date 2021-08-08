package com.chat.app.models.Dtos;

import com.chat.app.models.Chat;
import com.chat.app.models.UserModel;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;
    private String profileImage;
    private List<ChatDto> chats;
    private ChatDto chatWithUser;

    private boolean hasChatWithLoggedUser;

    public UserDto(UserModel userModel, List<Chat> chats){
        this(userModel);
        this.chats = chats.stream().map(ChatDto::new).collect(Collectors.toList());
    }

    public UserDto(UserModel userModel, Chat chatWithUser){
        this(userModel);
        this.chatWithUser = new ChatDto(chatWithUser);
    }

    public UserDto(UserModel userModel){
        this.id = userModel.getId();
        this.username = userModel.getUsername();
        this.age = userModel.getAge();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.country = userModel.getCountry();
        this.profileImage = userModel.getProfileImage().getName();
        this.role = userModel.getRole();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isHasChatWithLoggedUser() {
        return hasChatWithLoggedUser;
    }

    public void setHasChatWithLoggedUser(boolean hasChatWithLoggedUser) {
        this.hasChatWithLoggedUser = hasChatWithLoggedUser;
    }

    public List<ChatDto> getChats() {
        return chats;
    }

    public void setChats(List<ChatDto> chats) {
        this.chats = chats;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ChatDto getChatWithUser() {
        return chatWithUser;
    }

    public void setChatWithUser(ChatDto chatWithUser) {
        this.chatWithUser = chatWithUser;
    }
}
