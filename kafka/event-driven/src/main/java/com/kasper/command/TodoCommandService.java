package com.kasper.command;

import com.kasper.config.KafkaConfig;
import com.kasper.events.TodoCreatedEvent;
import com.kasper.events.TodoDeletedEvent;
import com.kasper.events.TodoUpdatedEvent;
import com.kasper.model.TodoItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoCommandService {

    private final TodoCommandRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public String handleCreateTodoCommand(CreateTodoCommand command) {
        // Create a new todo item
        TodoItem todoItem = TodoItem.create(command.getTitle(), command.getDescription());
        
        // Save to the command database
        repository.save(todoItem);
        
        // Publish the event
        TodoCreatedEvent event = new TodoCreatedEvent(
                todoItem.getId(),
                todoItem.getTitle(),
                todoItem.getDescription()
        );
        
        kafkaTemplate.send(KafkaConfig.TODO_EVENTS_TOPIC, todoItem.getId(), event);
        
        return todoItem.getId();
    }

    @Transactional
    public void handleUpdateTodoCommand(UpdateTodoCommand command) {
        // Find the todo item
        TodoItem todoItem = repository.findById(command.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo item not found with id: " + command.getId()));
        
        // Update the todo item
        todoItem.update(command.getTitle(), command.getDescription(), command.getCompleted());
        
        // Save the changes
        repository.save(todoItem);
        
        // Publish the event
        TodoUpdatedEvent event = new TodoUpdatedEvent(
                todoItem.getId(),
                todoItem.getTitle(),
                todoItem.getDescription(),
                todoItem.isCompleted()
        );
        
        kafkaTemplate.send(KafkaConfig.TODO_EVENTS_TOPIC, todoItem.getId(), event);
    }

    @Transactional
    public void handleDeleteTodoCommand(DeleteTodoCommand command) {
        // Check if the todo item exists
        if (!repository.existsById(command.getId())) {
            throw new EntityNotFoundException("Todo item not found with id: " + command.getId());
        }
        
        // Delete the todo item
        repository.deleteById(command.getId());
        
        // Publish the event
        TodoDeletedEvent event = new TodoDeletedEvent(command.getId());
        
        kafkaTemplate.send(KafkaConfig.TODO_EVENTS_TOPIC, command.getId(), event);
    }
}