package com.example.tapiwa.todoapp.InitializeApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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

    private FileHandler fileHandler;

    public InitializeApp(Context context) {
        this.fileHandler = new FileHandler(context);
    }

    @Override
    protected Boolean doInBackground(Boolean... booleans) {
        fileHandler.createFiles();
        return null;
    }
}
