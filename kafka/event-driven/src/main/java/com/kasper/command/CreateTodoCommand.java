package com.kasper.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateTodoCommand extends TodoCommand {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    public CreateTodoCommand(String title, String description) {
        super(null); // ID will be generated
        this.title = title;
        this.description = description;
    }
}