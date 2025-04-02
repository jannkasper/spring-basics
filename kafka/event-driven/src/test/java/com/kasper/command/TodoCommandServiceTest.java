package com.kasper.command;

import com.kasper.config.KafkaConfig;
import com.kasper.events.TodoCreatedEvent;
import com.kasper.model.TodoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoCommandServiceTest {

    @Mock
    private TodoCommandRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TodoCommandService commandService;

    @Captor
    private ArgumentCaptor<TodoItem> todoItemCaptor;

    @Captor
    private ArgumentCaptor<TodoCreatedEvent> eventCaptor;

    @Test
    void handleCreateTodoCommand_shouldSaveItemAndPublishEvent() {
        // Arrange
        CreateTodoCommand command = new CreateTodoCommand("Test Todo", "This is a test");
        when(repository.save(any(TodoItem.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        String id = commandService.handleCreateTodoCommand(command);

        // Assert
        assertNotNull(id);
        verify(repository).save(todoItemCaptor.capture());
        TodoItem savedItem = todoItemCaptor.getValue();
        assertEquals("Test Todo", savedItem.getTitle());
        assertEquals("This is a test", savedItem.getDescription());
        assertEquals(false, savedItem.isCompleted());

        verify(kafkaTemplate).send(eq(KafkaConfig.TODO_EVENTS_TOPIC), eq(savedItem.getId()), eventCaptor.capture());
        TodoCreatedEvent event = eventCaptor.getValue();
        assertEquals(savedItem.getId(), event.getId());
        assertEquals("Test Todo", event.getTitle());
        assertEquals("This is a test", event.getDescription());
    }
}