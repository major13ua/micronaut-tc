package com.example.testcontainer.controller;

import com.example.testcontainer.service.StorageService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/api/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Get("/{key}")
    public String getItem(String key) {
        return storageService.getItem(key).orElse("No item found for key=" + key);
    }

}
