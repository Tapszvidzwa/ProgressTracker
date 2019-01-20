package com.example.tapiwa.todoapp.Sync;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.tapiwa.todoapp.R;
import com.google.firebase.auth.FirebaseAuth;


public class Sync extends Activity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_syncing_files);
        syncFiles();
    }

    private void syncFiles() {
        new SyncHelper(getApplicationContext(), auth.getUid(), this).execute();
    }
}