package com.example.tapiwa.todoapp.sharedProjects;

public class SharedProjectTask {

    private String task;
    private String completionStatus;
    private String dateLastModified;

    public SharedProjectTask() {
    }

    public SharedProjectTask(String task, String status) {
        this.task = task;
        this.completionStatus = status;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(String dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }


}