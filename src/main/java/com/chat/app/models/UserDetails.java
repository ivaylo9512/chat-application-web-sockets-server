package com.chat.app.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.List;

public class UserDetails extends User {
    private UserModel userModel;
    private int id;

    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int id){
        super(username,password,authorities);
        this.id = id;
    }
    public UserDetails(UserModel userModel, List<SimpleGrantedAuthority> authorities){
        super(userModel.getUsername(), userModel.getPassword(), authorities);
        this.userModel = userModel;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
