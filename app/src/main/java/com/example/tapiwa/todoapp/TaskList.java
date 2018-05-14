package com.example.tapiwa.todoapp;

import java.util.LinkedList;

/**
 * Created by tapiwa on 3/28/18.
 */

public class TaskList {

    LinkedList<Task> taskList;

    public TaskList() {
    }

    public LinkedList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(LinkedList<Task> taskList) {
        this.taskList = taskList;
    }

}