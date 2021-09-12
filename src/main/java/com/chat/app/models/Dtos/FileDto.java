package com.chat.app.models.Dtos;

import com.chat.app.models.File;

public class FileDto {
    private long id;
    private String resourceType;
    private long ownerId;
    private String extension;
    private String type;
    private double size;

    public FileDto(){}

    public FileDto(File file) {
        this.id = file.getId();
        this.resourceType = file.getResourceType();
        this.ownerId = file.getOwner().getId();
        this.extension = file.getExtension();
        this.type = file.getType();
        this.size = file.getSize();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
