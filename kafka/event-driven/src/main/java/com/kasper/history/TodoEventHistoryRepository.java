package com.kasper.history;

import com.kasper.model.TodoEventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoEventHistoryRepository extends JpaRepository<TodoEventHistory, Long> {
    List<TodoEventHistory> findByTodoIdOrderByTimestampAsc(String todoId);
}