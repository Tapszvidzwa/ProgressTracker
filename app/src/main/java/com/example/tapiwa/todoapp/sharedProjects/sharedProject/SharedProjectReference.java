package com.example.tapiwa.todoapp.sharedProjects.sharedProject;

public class SharedProjectReference {

    String projectKey;
    String projectName;

    public SharedProjectReference() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
