package com.jayantirai.tasktracker.task;

import com.jayantirai.tasktracker.common.NotFoundException;
import com.jayantirai.tasktracker.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {
    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> list(String username) {
        return tasks.findByOwnerUsernameOrderByIdAsc(username).stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long id, String username) { return TaskResponse.from(find(id, username)); }

    public TaskResponse create(TaskRequest req, String username) {
        Task t = new Task();
        t.setOwner(users.findByUsername(username).orElseThrow());
        apply(t, req);
        return TaskResponse.from(tasks.save(t));
    }

    public TaskResponse update(Long id, TaskRequest req, String username) {
        Task t = find(id, username);
        apply(t, req);
        return TaskResponse.from(tasks.saveAndFlush(t));
    }

    public void delete(Long id, String username) { tasks.delete(find(id, username)); }

    @Transactional(readOnly = true)
    public List<TaskResponse> listAll() {
        return tasks.findAll().stream().map(TaskResponse::from).toList();
    }

    private Task find(Long id, String username) {
        return tasks.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("Task " + id + " not found"));
    }

    private void apply(Task t, TaskRequest req) {
        t.setTitle(req.title());
        t.setDescription(req.description());
        if (req.status() != null) t.setStatus(req.status());
    }
}
