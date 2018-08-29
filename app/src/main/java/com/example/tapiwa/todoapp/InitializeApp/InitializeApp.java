package com.example.tapiwa.todoapp.InitializeApp;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectTask;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerModel;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectModel;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SharedProjectTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;

public class InitializeApp extends AsyncTask<Boolean, Integer, Boolean> {

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
                fileHandler.saveFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE), createExampleTask());
                fileHandler.saveFile(context.getString(R.string.PERSONAL_PROJECTS_FILE), createEmptyPersonalProjectFolder());
    }

    private String createEmptyPersonalProjectFolder() {
        PersonalProjectModel projectModel = new PersonalProjectModel();
        projectModel.setProjectTitle("Sample project folder");
        projectModel.setProjectKey();
        projectModel.setLastModifiedtime(Long.toString(System.currentTimeMillis()));
        projectModel.setProjectTasks(new ArrayList<PersonalProjectTask>());
        LinkedList<PersonalProjectModel> list = new LinkedList<>();
        list.add(projectModel);
        PersonalProjectsContainerModel personalProjectsContainerModel = new PersonalProjectsContainerModel();
        personalProjectsContainerModel.setProjects(list);
        Gson gson = new Gson();
        return gson.toJson(personalProjectsContainerModel);
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


    @Override
    protected Boolean doInBackground(Boolean... booleans) {
        createFiles();
        return null;
    }
}
