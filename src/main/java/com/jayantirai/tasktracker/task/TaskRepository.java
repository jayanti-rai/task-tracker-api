package com.jayantirai.tasktracker.task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerUsernameOrderByIdAsc(String username);
    Optional<Task> findByIdAndOwnerUsername(Long id, String username);
}
