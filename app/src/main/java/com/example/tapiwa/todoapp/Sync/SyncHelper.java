package com.example.tapiwa.todoapp.Sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.tapiwa.todoapp.Utils.BackUp;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.home.MainActivity;

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
        while(!backUp.isSyncCompleted()) {
            //wait for sync to finish
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
        super.onPostExecute(aVoid);
    }
}

