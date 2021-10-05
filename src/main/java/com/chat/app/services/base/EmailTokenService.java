package com.chat.app.services.base;

import com.chat.app.models.EmailToken;
import com.chat.app.models.UserModel;
import javax.mail.MessagingException;

public interface EmailTokenService {
    void create(EmailToken token);

    EmailToken findByToken(String token);

    void delete(EmailToken token);

    void sendVerificationEmail(UserModel user) throws MessagingException;
}
