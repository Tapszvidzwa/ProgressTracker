package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.example.tapiwa.todoapp.login.User;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.sharedProject.SharedProjectReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.tapiwa.todoapp.Utils.Constants.SHARED_PROJECTS_DB_PATH;
import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;

public class DatabaseHandler {

    private FirebaseFirestore db;
    private ArrayList<SharedProjectReference> sharedProjectKeys;
    private FirebaseAuth auth;
    private ArrayList<SharedProjectModel> sharedProjectsList;

    public DatabaseHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedProjectKeys = new ArrayList<>();
        sharedProjectsList = new ArrayList<>();
    }

    public void addNewSharedProject(final Context context, String projectName) {
        final String uid = auth.getCurrentUser().getUid();
        final SharedProjectModel project = createNewSharedProject(projectName);
        final String key = createSharedProjectKey();
        addProjectToSharedProjectsDb(key, project);
        addSharedProjectToUserPortfolio(key, uid, context, projectName);
    }

    public void addSharedProjectToUserPortfolio(final String key, final String uid, final Context context, final String projectName) {
        db.document(USERS_DB_PATH + uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userCredentials = documentSnapshot.toObject(User.class);
                sharedProjectKeys.clear();
                sharedProjectKeys = userCredentials.getSharedProjectReferenceKeys();
                sharedProjectKeys.add(createNewSharedProjectReference(projectName, key));
                userCredentials.setSharedProjectReferenceKeys(sharedProjectKeys);
                updateUserSharedProjectsPortFolio(userCredentials, context);
            }
        });
    }

    private SharedProjectReference createNewSharedProjectReference(final String projectName, final String projectKey) {
        SharedProjectReference projectReference = new SharedProjectReference();
        projectReference.setProjectKey(projectKey);
        projectReference.setProjectName(projectName);
        return projectReference;
    }

    public void updateUserSharedProjectsPortFolio(User userCredentials, final Context context) {

        db.document(USERS_DB_PATH + userCredentials.getUid())
                .set(userCredentials)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Updated your projects", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addProjectToSharedProjectsDb(String key, SharedProjectModel project) {
        db.document(SHARED_PROJECTS_DB_PATH + key).set(project);
    }

    public SharedProjectModel createNewSharedProject(String projectName) {
        SharedProjectModel newProject = new SharedProjectModel();
        newProject.setProjectTitle(projectName);
        newProject.setLastModifiedtime(Long.toString(System.currentTimeMillis()));
        return newProject;
    }

    public String createSharedProjectKey() {
        return db.collection(SHARED_PROJECTS_DB_PATH).document().getId();
    }

}
