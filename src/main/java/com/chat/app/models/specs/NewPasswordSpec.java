package com.chat.app.models.specs;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

public class NewPasswordSpec {
    @NotNull
    private String username;

    @NotNull
    private String currentPassword;

    @Length(min = 8, max=20)
    private String newPassword;

    public NewPasswordSpec(String username, String currentPassword, String newPassword) {
        this.username = username;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public NewPasswordSpec() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
