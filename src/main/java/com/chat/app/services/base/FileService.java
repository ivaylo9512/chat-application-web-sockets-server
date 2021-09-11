package com.chat.app.services.base;

import com.chat.app.models.File;
import com.chat.app.models.UserModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;

public interface FileService {
    boolean delete(String resourceType, long ownerId, UserModel loggedUser);

    Resource getAsResource(String fileName) throws FileNotFoundException;

    File findByName(String resourceType, long ownerId);

    void save(String name, MultipartFile receivedFile);

    File generate(MultipartFile receivedFile, String resourceType, String fileType);
}