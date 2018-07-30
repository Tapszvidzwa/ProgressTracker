package com.example.tapiwa.todoapp.sharedProjects;

import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SharedProjectTask;

import java.util.ArrayList;

public class SharedProjectModel {

    private String projectTitle;
    private ArrayList<SharedProjectTask> projectTasks;
    private ArrayList<String> memberEmails;
    private String lastModifiedtime;
    private String projectKey;

    public SharedProjectModel() {
    }

    public ArrayList<String> getMemberEmails() {
        return memberEmails;
    }

    public void setMemberEmails(ArrayList<String> memberEmails) {
        this.memberEmails = memberEmails;
    }

    public String getLastModifiedtime() {
        return lastModifiedtime;
    }

    public void setLastModifiedtime(String lastModifiedtime) {
        this.lastModifiedtime = lastModifiedtime;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public ArrayList<SharedProjectTask> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(ArrayList<SharedProjectTask> projectTasks) {
        this.projectTasks = projectTasks;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }


}
