package com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment;

public class SharedProjectTask {

    private String task;
    private String completionStatus;
    private String dateLastModified;
    private String whoCompletedTask;

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

    public String getWhoLastModifiedTask() {
        return whoCompletedTask;
    }

    public void setWhoLastModifiedTask(String whoCompletedTask) {
        this.whoCompletedTask = whoCompletedTask;
    }
}