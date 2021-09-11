package com.chat.app.models.Dtos;

import com.chat.app.models.Chat;
import com.chat.app.models.File;
import com.chat.app.models.Request;
import com.chat.app.models.UserModel;
import com.chat.app.models.specs.UserSpec;

import java.util.List;

public class UserDto {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;
    private String profileImage;
    private List<ChatDto> chats;
    private ChatDto chatWithUser;
    private String requestState;
    private long requestId;

    public UserDto(UserModel userModel, Chat chatWithUser){
        this(userModel);
        this.chatWithUser = new ChatDto(chatWithUser);
        this.requestState = "completed";
    }

    public UserDto(){

    }

    public UserDto(UserModel userModel, Request request) {
        this(userModel);
        setRequestState(request);
    }

    public UserDto(UserSpec user, String role) {
        this(user.getId(), user.getUsername(), user.getEmail(), user.getAge(), user.getFirstName(),
                user.getLastName(), user.getCountry(), role);
    }

    public UserDto(UserModel user){
        this(user.getId(), user.getUsername(), user.getEmail(), user.getAge(), user.getFirstName(),
                user.getLastName(), user.getCountry(), user.getRole());
        setProfileImage(user.getProfileImage());
    }

    public UserDto(long id, String username, String email, int age, String firstName, String lastName, String country, String role){
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.role = role;
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

    public void setProfileImage(File profileImage) {
        if(profileImage != null){
            this.profileImage = profileImage.getResourceType() + profileImage.getOwner().getId() +
                    "." + profileImage.getExtension();
        }
    }

    public ChatDto getChatWithUser() {
        return chatWithUser;
    }

    public void setChatWithUser(ChatDto chatWithUser) {
        this.chatWithUser = chatWithUser;
    }

    public String getRequestState() {
        return requestState;
    }

    public void setRequestState(String requestState) {
        this.requestState = requestState;
    }

    public void setRequestState(Request request) {
        if(request != null){
            this.requestId = request.getId();
            if(request.getSender().getId() == id){
                this.requestState = "accept";
                return;
            }
            this.requestState = "pending";
            return;
        }
        this.requestState = "send";
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
