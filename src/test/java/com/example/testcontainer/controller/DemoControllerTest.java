package com.example.testcontainer.controller;

import com.example.testcontainer.resource.StorageResource;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertySource;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@MicronautTest
@Testcontainers
public class DemoControllerTest {

    private static final int REDIS_PORT = 6379;

    @Container
    static GenericContainer redis = new GenericContainer<>("redis:5.0.8-alpine")
            .withExposedPorts(REDIS_PORT);

    private static EmbeddedServer embeddedServer;
    private static ApplicationContext context;

    StorageResource storageResource;

    @BeforeAll
    public static void initUnitTest() {

        embeddedServer = ApplicationContext.run(EmbeddedServer.class, PropertySource.of(
                "test", Map.of("redis.host", redis.getContainerIpAddress(), "redis.port", redis.getMappedPort(REDIS_PORT))
        ));

        context = embeddedServer.getApplicationContext();

    }

    @BeforeEach
    public void initTest() {
        storageResource = context.getBean(StorageResource.class);
    }

    @Test
    void testItWorks() {
        Assertions.assertTrue(embeddedServer.isRunning());
    }

    @Test
    public void notFoundResponseTest() throws MalformedURLException {
        var key = "item-" + System.currentTimeMillis();
        HttpClient client = HttpClient.create(new URL("http://" + embeddedServer.getHost() + ":" + embeddedServer.getPort()));
        String response = client.toBlocking().retrieve(HttpRequest.GET("/api/storage/" + key), String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("No item found for key=" + key, response);
    }


    @Test
    public void foundResponseTest() throws MalformedURLException {
        var key = "item-" + System.currentTimeMillis();

        Optional<String> item = storageResource.getItem(key);
        assertThat(item).isEmpty();
        storageResource.putItem(key, "Hello TC!");

        HttpClient client = HttpClient.create(new URL("http://" + embeddedServer.getHost() + ":" + embeddedServer.getPort()));
        String response = client.toBlocking().retrieve(HttpRequest.GET("/api/storage/" +  key), String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Hello TC!", response);
    }

}
