package com.chat.app.models.DTOs;

import com.chat.app.models.Chat;
import com.chat.app.models.UserDetails;
import com.chat.app.models.UserModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDto {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;
    private String profilePicture;
    private Map<Integer, ChatDto> chats;
    private boolean hasChatWithLoggedUser;

    public UserDto(UserDetails userDetails){
        this.id = userDetails.getId();
        this.username    = userDetails.getUsername();
        this.age = userDetails.getAge();
        this.firstName = userDetails.getFirstName();
        this.lastName = userDetails.getLastName();
        this.country = userDetails.getCountry();
        this.profilePicture = userDetails.getProfilePicture();
        this.role = new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority();
    }
    public UserDto(UserDetails userDetails, Map<Integer, Chat> chats){
        this.id = userDetails.getId();
        this.username    = userDetails.getUsername();
        this.age = userDetails.getAge();
        this.firstName = userDetails.getFirstName();
        this.lastName = userDetails.getLastName();
        this.country = userDetails.getCountry();
        this.profilePicture = userDetails.getProfilePicture();
        this.role = new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority();
        this.chats = chats.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> new ChatDto((Chat)o)));
    }

    public UserDto(UserModel userModel, Map<Integer, Chat> chats){
        this.id = userModel.getId();
        this.username = userModel.getUsername();
        this.age = userModel.getAge();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.country = userModel.getCountry();
        this.profilePicture = userModel.getProfilePicture();
        this.role = userModel.getRole();
        this.chats = chats.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> new ChatDto((Chat)o)));
    }

    public UserDto(UserModel userModel){
        this.id = userModel.getId();
        this.username = userModel.getUsername();
        this.age = userModel.getAge();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.country = userModel.getCountry();
        this.profilePicture = userModel.getProfilePicture();
        this.role = userModel.getRole();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isHasChatWithLoggedUser() {
        return hasChatWithLoggedUser;
    }

    public void setHasChatWithLoggedUser(boolean hasChatWithLoggedUser) {
        this.hasChatWithLoggedUser = hasChatWithLoggedUser;
    }

    public Map<Integer, ChatDto> getChats() {
        return chats;
    }

    public void setChats(Map<Integer, ChatDto> chats) {
        this.chats = chats;
    }
}
