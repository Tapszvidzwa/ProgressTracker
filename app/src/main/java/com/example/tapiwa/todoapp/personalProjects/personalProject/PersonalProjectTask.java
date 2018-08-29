package com.example.tapiwa.todoapp.personalProjects.personalProject;

public class PersonalProjectTask {

    private String task;
    private String completionStatus;
    private String dateLastModified;

    public PersonalProjectTask() {
    }

    public PersonalProjectTask(String task, String status) {
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

    public void setDefaultCompletionStatus() {
        this.completionStatus = "uncompleted";
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}