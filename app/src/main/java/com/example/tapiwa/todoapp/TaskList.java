package com.example.tapiwa.todoapp;

import java.util.LinkedList;

/**
 * Created by tapiwa on 3/28/18.
 */

public class TaskList {

    LinkedList<Task> taskList;
    String name;
    boolean trackProgress = true;

    public TaskList() {
    }

    public LinkedList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(LinkedList<Task> taskList) {
        this.taskList = taskList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumUncompletedTasks() {
        int num_uncompleted = 0;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getStatus().equals("uncompleted")) {
                ++num_uncompleted;
            }
        }

        return num_uncompleted;
    }

    public int getNumCompletedTasks() {
        int completed = 0;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).equals("completed")) {
                ++completed;
            }
        }
        return completed;
    }

    public void setTrackProgress(boolean b) {
        this.trackProgress = b;
    }

    public boolean getTrackProgress() {
        return trackProgress;
    }
}