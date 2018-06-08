package com.example.tapiwa.todoapp;

import java.util.LinkedList;

/**
 * Created by tapiwa on 3/28/18.
 */

public class ProjectsList {

    LinkedList<TaskList> taskList;

    public ProjectsList() {
    }

    public LinkedList<TaskList> getTaskList() {
        return taskList;
    }

    public void setTaskList(LinkedList<TaskList> taskList) {
        this.taskList = taskList;
    }

}