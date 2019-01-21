package com.example.tapiwa.todoapp.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectModel;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectTask;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import es.dmoral.toasty.Toasty;

public class FileHandler {

    private Context context;

    public FileHandler() {
    }

    public FileHandler(Context context) {
        this.context = context;
    }

    public void saveFile(String filename, String json) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir(), filename);
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] personalProjectsFileBytes = json.getBytes();
            fos.write(personalProjectsFileBytes);
            fos.flush();
        } catch (IOException e) {
            Toasty.error(context, context.getString(R.string.failed_file_creation), Toast.LENGTH_SHORT).show();
            return;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject readFile(final String filename) {
        BufferedReader br = null;
        try {
            File tasksFile = new File(context.getFilesDir(), filename);
            tasksFile.createNewFile();

            br = new BufferedReader(new FileReader(tasksFile));
            if (br != null) {
                if (br.read() != -1) {
                    String tasksString = '{' + br.readLine();
                    JSONObject jsonObject = new JSONObject(tasksString);
                    return jsonObject;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            Toasty.error(context, context.getString(R.string.failed_file_loading), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void clearAllFiles() {
        saveFile(context.getString(R.string.DAILY_TASKS_FILE), "");
        saveFile(context.getString(R.string.WEEKLY_TASKS_FILE), "");
        saveFile(context.getString(R.string.YEARLY_TASKS_FILE), "");
        saveFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE), "");
        saveFile(context.getString(R.string.PERSONAL_PROJECTS_FILE), "");
    }

    public void createFiles() {
        saveFile(context.getString(R.string.DAILY_TASKS_FILE), createExampleTask());
        saveFile(context.getString(R.string.WEEKLY_TASKS_FILE), createExampleTask());
        saveFile(context.getString(R.string.YEARLY_TASKS_FILE), createExampleTask());
        saveFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE), createExampleTask());
        saveFile(context.getString(R.string.PERSONAL_PROJECTS_FILE), createEmptyPersonalProjectFolder());
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

    public String getNumTasksCompleted(String filename) {
        JSONObject tasksJson = readFile(filename);
        Gson gson = new Gson();
        TaskList list = gson.fromJson(tasksJson.toString(), TaskList.class);
        if (list.getNumCompletedTasks() == 0) {
            return "";
        } else {
            return String.valueOf(list.getNumCompletedTasks());
        }
    }

    public String getNumTasksUncompleted(String filename) {
        if (filename.equals(context.getString(R.string.PERSONAL_PROJECTS_FILE))) {
            return getNumUncompletedPersonalProjects();
        }
        JSONObject tasksJson = readFile(filename);
        Gson gson = new Gson();
        TaskList list = gson.fromJson(tasksJson.toString(), TaskList.class);
        if (list.getNumUncompletedTasks() == 0) {
            return "";
        } else {
            return String.valueOf(list.getNumUncompletedTasks());
        }
    }

    private String getNumUncompletedPersonalProjects() {
        JSONObject tasksJson = readFile(context.getString(R.string.PERSONAL_PROJECTS_FILE));
        Gson gson = new Gson();
        PersonalProjectsContainerModel list = gson.fromJson(tasksJson.toString(), PersonalProjectsContainerModel.class);

        if (list.getNumUncompletedProjects().equals("0")) {
            return "";
        } else {
            return list.getNumUncompletedProjects();
        }

    }
}
