package com.chat.app.services;

import com.chat.app.exceptions.FileNotFoundUncheckedException;
import com.chat.app.exceptions.FileStorageException;
import com.chat.app.services.base.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {
    private final Path fileLocation;

    public FileServiceImpl() {
        this.fileLocation = Paths.get("./uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create directory");
        }
    }

    @Override
    public File update(MultipartFile file, String name, long id){
        File updatedFile = create(file, name);
        updatedFile.setId(id);
        return fileRepository.save(updatedFile);
    }

    @Override
    public File create(MultipartFile receivedFile, String name) {

        File file = generate(receivedFile, name);

        try {
            if (!file.getType().startsWith("image/")) {
                throw new FileFormatException("File should be of type IMAGE.");
            }

            save(file, receivedFile);

            return file;

        } catch (IOException e) {
            throw new FileStorageException("Couldn't store the image.");
        }
    }

    @Override
    public Resource getFileAsResource(String fileName){
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundUncheckedException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundUncheckedException("File not found " + e);
        }
    }

    private void save(File image, MultipartFile receivedFile) throws IOException {
        Path targetLocation = this.fileLocation.resolve(image.getName());
        Files.copy(receivedFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private File generate(MultipartFile receivedFile, String name) {
        String fileType = FilenameUtils.getExtension(receivedFile.getOriginalFilename());
        String fileName = name + "." + fileType;
        return new File(fileName, receivedFile.getSize(), receivedFile.getContentType());
    }
}
