package com.example.testcontainer.resource;

import io.lettuce.core.api.StatefulRedisConnection;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class StorageResource {

    @NotNull private final StatefulRedisConnection<String, String> redisConnection;

    public StorageResource(StatefulRedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    public Optional<String> getItem(String key) {
        return Optional.ofNullable(redisConnection.sync().get(key));
    }

    public String putItem(String key, String value) {
        return redisConnection.sync().setex(key, 30, value);
    }

}
