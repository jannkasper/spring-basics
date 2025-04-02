package com.kasper.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateTodoCommand extends TodoCommand {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Completed status is required")
    private Boolean completed;
    
    public UpdateTodoCommand(String id, String title, String description, Boolean completed) {
        super(id);
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}