package com.example.testcontainer.service;

import com.example.testcontainer.resource.StorageResource;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertySource;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@Testcontainers
public class DemoServiceTest {

    private static final int REDIS_PORT = 6379;

    StorageResource storageResource;
    StorageService storageService;

    @Container
    static GenericContainer redis = new GenericContainer<>("redis:5.0.8-alpine")
            .withExposedPorts(REDIS_PORT);

    private static ApplicationContext context;

    @BeforeAll
    public static void initUnitTest() {
        context = ApplicationContext.run(PropertySource.of(
                "test", Map.of("redis.host", redis.getContainerIpAddress(), "redis.port", redis.getMappedPort(REDIS_PORT))
        ));
    }

    @BeforeEach
    public void initTest() {
        storageResource = context.getBean(StorageResource.class);
        storageService = context.getBean(StorageService.class);
    }

    @Test
    void shouldNotFindAnyData() {
        Optional<String> noKeyFound = storageService.getItem("noKeyFound");
        assertThat(noKeyFound).isEmpty();
    }

    @Test
    void findFindDataInStorage() {
        var key = "item-" + System.currentTimeMillis();

        Optional<String> item = storageResource.getItem(key);
        assertThat(item).isEmpty();
        storageResource.putItem(key, "Hello TC!");

        Optional<String> keyFound = storageService.getItem(key);
        assertThat(keyFound).isNotEmpty();
        assertThat(keyFound).get().isEqualTo("Hello TC!");
    }

}
