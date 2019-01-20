package com.example.tapiwa.todoapp.InitializeApp;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tapiwa.todoapp.Utils.FileHandler;

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
