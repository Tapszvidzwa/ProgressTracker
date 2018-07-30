package com.example.tapiwa.todoapp.InitializeApp;

import android.content.Context;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectListModel;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;

public class InitializeApp {

    private Context context;
    private FileHandler fileHandler;

    public InitializeApp(Context context) {
        this.context = context;
        fileHandler = new FileHandler(context);
    }

    public void createFiles() {
        fileHandler.saveFile(context.getString(R.string.DAILY_TASKS_FILE), createExampleTask());
        fileHandler.saveFile(context.getString(R.string.WEEKLY_TASKS_FILE), createExampleTask());
        fileHandler.saveFile(context.getString(R.string.YEARLY_TASKS_FILE), createExampleTask());
        fileHandler.saveFile(context.getString(R.string.PERSONAL_PROJECTS_FILE), createEmptyPersonalProjectFolder());
        fileHandler.saveFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE), createExampleTask());
    }

    private String createEmptyPersonalProjectFolder() {
        PersonalProjectModel projectModel = new PersonalProjectModel();
        projectModel.setProjectTitle("Sample project folder");
        ArrayList<PersonalProjectModel> list = new ArrayList<>();
        list.add(projectModel);
        PersonalProjectListModel projectListModel = new PersonalProjectListModel();
        projectListModel.setProjects(list);
        Gson gson = new Gson();
        return gson.toJson(projectListModel);
    }

    private String createExampleTask() {
        Task task = new Task();
        task.setStatus("uncompleted");
        task.setTask("Sample task");
        LinkedList<Task> list = new LinkedList<>();
        list.add(task);
        TaskList taskList = new TaskList();
        taskList.setTaskList(list);
        Gson gson = new Gson();
        return gson.toJson(taskList);
    }

    private int getPersonalProjectsCount() {
        return 0;
    }

    private int getSharedProjectsCount() {
        return 0;
    }
}
