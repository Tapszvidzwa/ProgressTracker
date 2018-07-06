package com.example.tapiwa.todoapp.sharedProjects;

import java.io.Serializable;
import java.util.ArrayList;

public class SharedProjectsListModel implements Serializable {

    private ArrayList<String> projects;

    public SharedProjectsListModel() {
        //empty constructor
    }

    public ArrayList<String> getProjects() {
        return projects;
    }

}
