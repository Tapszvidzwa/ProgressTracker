package com.example.tapiwa.todoapp.InitializeApp;

import android.app.Activity;
import android.content.Context;

import com.example.tapiwa.todoapp.R;

import java.io.File;
import java.io.IOException;

public class InitializeApp {

    private Context context;

    public InitializeApp(Context context) {
        this.context = context;
    }

    public void createFiles() {

        try {
            File dailyGoalsFile = new File(context.getFilesDir(), context.getString(R.string.DailyGoalsFile));
            dailyGoalsFile.createNewFile();

            File weeklyGoalsFile = new File(context.getApplicationContext().getFilesDir(), context.getString(R.string.Weekly_tasks_file));
            weeklyGoalsFile.createNewFile();


            File yearlyGoalsFile = new File(context.getApplicationContext().getFilesDir(), context.getString(R.string.Yearly_tasks_file));
            yearlyGoalsFile.createNewFile();

            File personalProjectsFile = new File(context.getApplicationContext().getFilesDir(), context.getString(R.string.Personal_projects_file));
            personalProjectsFile.createNewFile();

        } catch (IOException e) {
            //TODO: handle appropriately
        }

        return;
    }


    private int getPersonalProjectsCount() {

        return 0;
    }

    private int getSharedProjectsCount() {

        return 0;
    }
}
