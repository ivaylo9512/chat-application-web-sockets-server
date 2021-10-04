package com.chat.app.services.base;

import com.chat.app.models.EmailToken;
import com.chat.app.models.UserModel;
import javax.mail.MessagingException;

public interface EmailTokenService {
    void createVerificationToken(UserModel user, String token);

    EmailToken getToken(String token);


    void delete(EmailToken token);

    void sendVerificationEmail(UserModel user) throws MessagingException;
}
