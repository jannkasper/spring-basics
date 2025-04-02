package com.kasper.command;

import com.kasper.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoCommandRepository extends JpaRepository<TodoItem, String> {
}