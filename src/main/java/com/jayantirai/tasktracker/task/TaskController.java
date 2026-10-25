package com.jayantirai.tasktracker.task;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public List<TaskResponse> list(Principal p) { return service.list(p.getName()); }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id, Principal p) { return service.get(id, p.getName()); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest req, Principal p) { return service.create(req, p.getName()); }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest req, Principal p) {
        return service.update(id, req, p.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Principal p) { service.delete(id, p.getName()); }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponse> listAll() { return service.listAll(); }
}
