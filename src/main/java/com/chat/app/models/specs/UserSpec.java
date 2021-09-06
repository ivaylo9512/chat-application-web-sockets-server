package com.chat.app.models.specs;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserSpec {
    @NotNull
    private long id;

    @Length(min = 8, max=20)
    private String username;

    @Email
    @NotNull
    private String email;

    @NotNull(message = "You must provide first name.")
    private String firstName;

    @NotNull(message = "You must provide last name.")
    private String lastName;

    @NotNull
    private int age;

    @NotNull
    private String country;

    public UserSpec() {

    }

    public UserSpec(long id, String username, String email, String firstName, String lastName, int age, String country) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
        this.email = email;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
