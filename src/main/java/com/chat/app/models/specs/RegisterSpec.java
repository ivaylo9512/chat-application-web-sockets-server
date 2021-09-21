package com.chat.app.models.specs;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class RegisterSpec {
    @Length(min = 8, max=20, message = "Username must be between 8 and 20 characters.")
    @NotNull(message = "You must provide username.")
    private String username;

    @Email(message = "Must be a valid email.")
    @NotNull(message = "You must provide an email.")
    private String email;

    private MultipartFile profileImage;

    @Length(min = 10, max=25, message = "Password must be between 10 and 25 characters.")
    @NotNull(message = "You must provide password.")
    private String password;

    @NotNull(message = "You must provide first name.")
    private String firstName;

    @NotNull(message = "You must provide last name.")
    private String lastName;

    @NotNull(message = "You must provide country.")
    private String country;

    @NotNull(message = "You must provide age.")
    private Integer age;


    public RegisterSpec(String username, String password, String email, MultipartFile profileImage, String firstName, String lastName, String country, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
    }

    public RegisterSpec() {

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

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
