package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.tapiwa.todoapp.Utils.Constants.SHARED_PROJECTS_DB_PATH;

public class DatabaseHandler {

    private FirebaseFirestore db;

    public DatabaseHandler() {
        db = FirebaseFirestore.getInstance();
    }

    public void addNewSharedProject(final Context context, String projectName) {
        String key = db.collection(SHARED_PROJECTS_DB_PATH).getId();
        SharedProjectModel sharedProjectModel = new SharedProjectModel();
        sharedProjectModel.setProjectTitle(projectName);
        sharedProjectModel.setLastModifiedtime(Long.toString(System.currentTimeMillis()));
        db.document(SHARED_PROJECTS_DB_PATH + key).set(sharedProjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "New Project created", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}
