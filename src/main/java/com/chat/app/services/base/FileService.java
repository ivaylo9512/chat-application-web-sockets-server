package com.chat.app.services.base;

import org.springframework.core.io.Resource;
import java.io.FileNotFoundException;

public interface FileService {
    Resource loadFileAsResource(String fileName) throws FileNotFoundException;
}