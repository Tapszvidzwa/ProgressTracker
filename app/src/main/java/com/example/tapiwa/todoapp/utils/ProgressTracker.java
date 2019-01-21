package com.example.tapiwa.todoapp.utils;

import android.content.Context;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.TaskList;
import com.google.gson.Gson;

public class ProgressTracker {

    private Context context;
    private FileHandler fileHandler;
    private String filename;

    public ProgressTracker(Context context, String filename) {
        this.context = context;
        this.fileHandler = new FileHandler(context);
        this.filename = filename;
    }

    public ProgressTracker() {
    }

    public String getUncompleted_daily_tasks() {
        return fileHandler.getNumTasksUncompleted(context.getString(R.string.DAILY_TASKS_FILE));
    }

    public String getUncompleted_weekly_tasks() {
        return fileHandler.getNumTasksUncompleted(context.getString(R.string.WEEKLY_TASKS_FILE));
    }

    public String getUncompleted_yearly_tasks() {
        return fileHandler.getNumTasksUncompleted(context.getString(R.string.YEARLY_TASKS_FILE));
    }

    public String getUncompleted_long_term_tasks() {
        return fileHandler.getNumTasksUncompleted(context.getString(R.string.LONG_TERM_PROJECTS_FILE));
    }

    public String getUncompleted_personal_projects() {
        return fileHandler.getNumTasksUncompleted(context.getString(R.string.PERSONAL_PROJECTS_FILE));
    }

    public String getUncompleted_shared_projects() {
        return "";
    }

    public void updateCounter(TaskList list) {
        Gson gson = new Gson();
        fileHandler.saveFile(filename, gson.toJson(list));
    }

}
