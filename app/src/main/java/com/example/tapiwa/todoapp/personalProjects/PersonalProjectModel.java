package com.example.tapiwa.todoapp.personalProjects;

import com.example.tapiwa.todoapp.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class PersonalProjectModel {
    private String projectTitle;
    private ArrayList<Task> projectTask;

    public PersonalProjectModel() {
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public ArrayList<Task> getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ArrayList<Task> projectTask) {
        this.projectTask = projectTask;
    }

}
