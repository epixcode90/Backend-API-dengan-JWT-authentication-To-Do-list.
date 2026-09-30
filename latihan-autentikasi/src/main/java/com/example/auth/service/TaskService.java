package com.example.auth.service;

import com.example.auth.dto.CreateTaskRequest;
import com.example.auth.dto.TaskResponse;
import com.example.auth.dto.UpdateTaskRequest;
import com.example.auth.model.Task;
import com.example.auth.repository.TaskRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final TaskRepository repo = new TaskRepository();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public TaskResponse create(Long userId, CreateTaskRequest req){


        if(req.title == null || req.title.isEmpty()){
            throw new IllegalArgumentException("title Tidak boleh kosong");
        }

        String title = req.title.trim();

        if(title.length() < 3){
            throw new IllegalArgumentException("title minimal 3 characters");
        }

        if(title.length() > 100){
            throw new IllegalArgumentException("title maximal 100 characters");
        }

        Task task = new Task();
        task.setUser_id(userId);
        task.setTitle(title);
        task.setDescription(req.description != null? req.description.trim() : null);
        task.setCompleted(false);

        Task saved = repo.save(task);

        return toResponse(saved);

    }

    public List<TaskResponse> listAll(Long userId){
        List<Task> tasks = repo.findAllByUserId(userId);

        List<TaskResponse> taskResponses = new ArrayList<>();
        for(Task task : tasks){
            taskResponses.add(toResponse(task));
        }
        return taskResponses;
    }

    public TaskResponse getOne(Long userId, Long taskId){
        Task task = repo.findByIdAndUserId(taskId, userId);

        if(task == null){
            throw new IllegalArgumentException("Task tidak ditemukan");
        }

        return toResponse(task);
    }

    public TaskResponse update(Long userId, Long taskId, UpdateTaskRequest req){
        Task task = repo.findByIdAndUserId(taskId, userId);

        if(task == null){
            throw new IllegalArgumentException("Task tidak ditemukan");
        }

        if(req.title != null ){
            String title = req.title.trim();
            if(title.length() < 3){
                throw new IllegalArgumentException("title minimal 3 characters");
            }
            if(title.length() > 100){
                throw new IllegalArgumentException("title maximal 100 characters");
            }
            task.setTitle(title);
        }

        if(req.description != null ){
            task.setDescription(req.description.trim());
        }

        if(req.completed != null){
            task.setCompleted(req.completed);
        }

        boolean updated = repo.update(task);

        if(!updated){
            throw new IllegalArgumentException("gagal update task");
        }

        Task refresh = repo.findByIdAndUserId(taskId, userId);
        return toResponse(refresh);
    }

    public void delete(Long userId, Long taskId){
        boolean deleted = repo.deleteByIddanUserId(taskId, userId);
        if(!deleted){
            throw new IllegalArgumentException("gagal delete task");
        }
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt() != null
                        ? task.getCreatedAt().format(DATE_TIME_FORMATTER)
                        : null,
                task.getUpdatedAt() != null
                        ? task.getUpdatedAt().format(DATE_TIME_FORMATTER)
                        : null
        );
    }
}
