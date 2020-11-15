package com.chat.app.services.base;

import com.chat.app.models.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;

public interface FileService {
    Resource getFileAsResource(String fileName) throws FileNotFoundException;

    File update(MultipartFile file, String name, long id);

    File create(MultipartFile receivedFile, String name);
}