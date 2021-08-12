package com.chat.app.models.specs;

import javax.validation.constraints.Size;

public class UserSpec {

    @Size(min=7, max=22, message="Name should be be between 7 and 18 char.")
    private String username;

    private String firstName;

    private String lastName;

    private int age;

    private String country;

    public UserSpec() {

    }

    public UserSpec(String username, String firstName, String lastName, int age, String country) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
