package com.chat.app.services.base;

import com.chat.app.models.File;
import com.chat.app.models.UserModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;

public interface FileService {
    boolean delete(String fileName, UserModel loggedUser);

    Resource getAsResource(String fileName) throws FileNotFoundException;

    File update(MultipartFile file, String name, long id, String type);

    File create(MultipartFile receivedFile, String name, String type, UserModel owner);

    File findByName(String fileName);
}