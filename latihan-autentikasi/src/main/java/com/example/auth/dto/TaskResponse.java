package com.example.auth.dto;

public class TaskResponse {

    public Long id;
    public String title;
    public String description;
    public boolean completed;
    public String created_at;
    public String updated_at;


    public TaskResponse(Long id, String title, String description, boolean completed, String created_at, String updated_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
