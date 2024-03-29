package com.chat.app.models;

import com.chat.app.models.specs.RegisterSpec;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File profileImage;

    @Column(name = "is_enabled")
    private boolean isEnabled = false;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private String country;
    private String role;

    public UserModel(){
    }

    public UserModel(String username, String email, String password, String role, String firstName,
                     String lastName, int age, String country) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.country = country;
        this.role = role;
    }

    public UserModel(long id, String username, String email, String password, String role, String firstName,
                     String lastName, int age, String country) {
        this(username, email, password, role,  firstName, lastName, age, country);
        this.id = id;
    }

    public UserModel(RegisterSpec newUser, File profileImage, String role) {
        this(newUser.getUsername(), newUser.getEmail(), newUser.getPassword(), role, newUser.getFirstName(),
                newUser.getLastName(), newUser.getAge(), newUser.getCountry());
        setProfileImage(profileImage);
    }

    public UserModel(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserModel(String username, String email, String password, String role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserModel(long id, String username, String password, String role){
        this(username, password, role);
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public File getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(File profileImage) {
        if(profileImage != null){
            this.profileImage = profileImage;
            profileImage.setOwner(this);
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
