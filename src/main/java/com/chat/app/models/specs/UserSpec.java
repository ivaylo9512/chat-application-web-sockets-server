package com.chat.app.models.specs;



import com.chat.app.models.UserModel;

import javax.validation.constraints.Size;

public class UserSpec {

    @Size(min=7, max=22, message="Name should be be between 7 and 18 char.")
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private int age;

    private String country;

    private String role;

    public UserSpec() {

    }
    public UserSpec(UserModel user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.country = user.getCountry();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
