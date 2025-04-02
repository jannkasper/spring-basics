package com.kasper.query;

import com.kasper.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoQueryRepository extends JpaRepository<TodoItem, String> {
    List<TodoItem> findByCompleted(boolean completed);
}