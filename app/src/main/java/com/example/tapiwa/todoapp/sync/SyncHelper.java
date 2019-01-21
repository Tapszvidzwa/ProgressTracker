package com.example.tapiwa.todoapp.sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.tapiwa.todoapp.utils.BackUp;
import com.example.tapiwa.todoapp.utils.FileHandler;
import com.example.tapiwa.todoapp.navigation.NavigationController;

public class SyncHelper extends AsyncTask<Void, Integer, Void> {

    FileHandler fileHandler;
    BackUp backUp;
    Activity activity;

    public SyncHelper(Context context, String uid, Activity activity) {
        this.fileHandler = new FileHandler(context);
        this.backUp = new BackUp(context, uid);
        this.activity = activity;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        fileHandler.createFiles();
        backUp.runSyncLocalFiles();
        while (!backUp.isSyncCompleted()) {
            //wait for sync to finish
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent intent = new Intent(activity, NavigationController.class);
        activity.startActivity(intent);
        activity.finish();
        super.onPostExecute(aVoid);
    }
}

