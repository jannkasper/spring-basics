package com.kasper.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteTodoCommand extends TodoCommand {
    
    public DeleteTodoCommand(@NotBlank(message = "ID is required") String id) {
        super(id);
    }
}