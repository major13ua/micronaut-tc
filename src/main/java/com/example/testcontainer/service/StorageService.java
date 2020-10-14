package com.example.testcontainer.service;

import com.example.testcontainer.resource.StorageResource;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Slf4j
public class StorageService {

    private final StorageResource storageResource;

    public StorageService(StorageResource storageResource) {
        this.storageResource = storageResource;
    }

    public Optional<String> getItem(String key) {
        return storageResource.getItem(key);
    }

}
