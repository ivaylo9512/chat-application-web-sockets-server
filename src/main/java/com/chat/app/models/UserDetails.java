package com.chat.app.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.List;

public class UserDetails extends User {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String profilePicture;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id){
        super(username,password,authorities);
        this.id = id;
    }
    public UserDetails(UserModel userModel, List<SimpleGrantedAuthority> authorities){

        super(userModel.getUsername(), userModel.getPassword(), authorities);
        this.id = userModel.getId();
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.age = userModel.getAge();
        this.country = userModel.getCountry();
        this.profilePicture = userModel.getProfilePicture();
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

    public void setlastName(String lastName) {
        this.lastName = lastName;
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
}
