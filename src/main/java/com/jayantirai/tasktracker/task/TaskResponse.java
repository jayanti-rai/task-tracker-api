package com.jayantirai.tasktracker.task;

import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, String description, TaskStatus status,
                           String owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
    static TaskResponse from(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                t.getOwner().getUsername(), t.getCreatedAt(), t.getUpdatedAt());
    }
}
