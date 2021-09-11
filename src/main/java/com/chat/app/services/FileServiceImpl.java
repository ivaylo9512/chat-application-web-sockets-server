package com.chat.app.services;

import com.chat.app.exceptions.FileFormatException;
import com.chat.app.exceptions.FileStorageException;
import com.chat.app.exceptions.UnauthorizedException;
import com.chat.app.models.File;
import com.chat.app.models.UserModel;
import com.chat.app.repositories.base.FileRepository;
import com.chat.app.services.base.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {
    private final Path fileLocation;
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.fileLocation = Paths.get("./uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create directory");
        }
    }

    @Override
    public boolean delete(String resourceType, long ownerId, UserModel loggedUser) {
        if(ownerId != loggedUser.getId()
                && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized");
        }

        File file = findByName(resourceType, ownerId);
        if(file == null){
            throw new EntityNotFoundException("File not found.");
        }

        boolean isDeleted = new java.io.File("./uploads/" + resourceType + ownerId + "." + file.getExtension()).delete();
        if(isDeleted){
            fileRepository.delete(file);
            return true;
        }

        return false;
    }

    @Override
    public Resource getAsResource(String fileName){
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new EntityNotFoundException("File not found");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new FileFormatException(e.getMessage());
        }
    }

    @Override
    public File findByName(String resourceType, long ownerId){
        return fileRepository.findByName(resourceType, ownerId);
    }

    @Override
    public void save(String name, MultipartFile receivedFile) {
        try {
            String extension = FilenameUtils.getExtension(receivedFile.getOriginalFilename());
            String fileName = name + "." + extension;

            Path targetLocation = this.fileLocation.resolve(fileName);
            Files.copy(receivedFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Couldn't store the image.");
        }
    }

    @Override
    public File generate(MultipartFile receivedFile, String resourceType, String fileType) {
        String extension = FilenameUtils.getExtension(receivedFile.getOriginalFilename());
        String contentType = receivedFile.getContentType();

        if (contentType == null || !contentType.startsWith(fileType)) {
            throw new FileFormatException("File should be of type " + fileType);
        }

        return new File(resourceType, receivedFile.getSize(), receivedFile.getContentType(), extension);
    }
}
