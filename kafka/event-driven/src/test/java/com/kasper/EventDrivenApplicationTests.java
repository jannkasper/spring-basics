package com.kasper;

import com.kasper.command.CreateTodoCommand;
import com.kasper.model.TodoItem;
import com.kasper.query.TodoQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EventDrivenApplicationTests {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TodoQueryRepository queryRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void todoCreationShouldUpdateReadModel() {
        // Arrange
        CreateTodoCommand command = new CreateTodoCommand("Integration Test Todo", "Testing with TestContainers");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateTodoCommand> request = new HttpEntity<>(command, headers);

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/todos/commands",
                request,
                Map.class
        );

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        String todoId = responseBody.get("id");
        assertNotNull(todoId);

        // Wait for the event to be processed and the read model to be updated
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            TodoItem todoItem = queryRepository.findById(todoId).orElse(null);
            assertNotNull(todoItem);
            assertEquals("Integration Test Todo", todoItem.getTitle());
            assertEquals("Testing with TestContainers", todoItem.getDescription());
            assertEquals(false, todoItem.isCompleted());
        });
    }
}
