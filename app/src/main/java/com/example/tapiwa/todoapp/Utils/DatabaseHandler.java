package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tapiwa.todoapp.login.User;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectReference;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SharedProjectTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.Utils.Constants.SHARED_PROJECTS_DB_PATH;
import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;

public class DatabaseHandler {

    //TODO, implement granular updates/merges to firestore documents
    private FirebaseFirestore db;
    private ArrayList<SharedProjectReference> sharedProjectKeys;
    private FirebaseAuth auth;
    private ArrayList<SharedProjectModel> sharedProjectsList;
    public static String TAG = "DatabaseHandler";

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
        project.setProjectKey(key);
        addProjectToSharedProjectsDb(key, project);
        addSharedProjectToUserPortfolio(context, key, uid);
    }

    public void addSharedProjectToUserPortfolio(final Context context, final String key, final String uid) {
        db.document(USERS_DB_PATH + uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userCredentials = documentSnapshot.toObject(User.class);
                sharedProjectKeys.clear();
                sharedProjectKeys = userCredentials.getSharedProjectReferenceKeys();
                sharedProjectKeys.add(createNewSharedProjectReference(key));
                userCredentials.setSharedProjectReferenceKeys(sharedProjectKeys);
                updateUserInDB(userCredentials, context);
            }
        });
    }

    private SharedProjectReference createNewSharedProjectReference(final String projectKey) {
        SharedProjectReference projectReference = new SharedProjectReference();
        projectReference.setProjectKey(projectKey);
        return projectReference;
    }

    public void updateUserInDB(User userCredentials, final Context context) {
        db.document(USERS_DB_PATH + userCredentials.getUid())
                .set(userCredentials)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "New member added", Toast.LENGTH_SHORT).show();
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

        ArrayList<String> memberEmails = new ArrayList<>();
        ArrayList<String> memberNames = new ArrayList<>();
        memberNames.add(auth.getCurrentUser().getDisplayName());
        memberEmails.add(auth.getCurrentUser().getEmail());
        newProject.setMemberEmails(memberEmails);
        newProject.setMemberNames(memberNames);
        return newProject;
    }

    public String createSharedProjectKey() {
        return db.collection(SHARED_PROJECTS_DB_PATH).document().getId();
    }

    public void addSharedProjectTaskToDb(final Context context, final SharedProjectTask task, final String projectKey) {
        db.document(SHARED_PROJECTS_DB_PATH + projectKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SharedProjectModel project = documentSnapshot.toObject(SharedProjectModel.class);
                ArrayList<SharedProjectTask> projectTasks = project.getProjectTasks();
                if (projectTasks == null) {
                    projectTasks = new ArrayList<>();
                }
                projectTasks.add(task);
                project.setProjectTasks(projectTasks);
                updateSharedProjectInDb(context,project,projectKey);
            }
        });
    }

    public void renameSharedProject(final Context context, final String newProjectName, final SharedProjectReference projectReference) {

        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SharedProjectModel projectModel = documentSnapshot.toObject(SharedProjectModel.class);
                projectModel.setProjectTitle(newProjectName);
                updateSharedProjectInDb(context, projectModel, projectReference.getProjectKey());
            }
        });

    }

    public void updateSharedProjectInDb(final Context context, SharedProjectModel project, final String projectKey) {
        db.document(SHARED_PROJECTS_DB_PATH + projectKey).set(project);
    }

    public void addMemberToSharedProject(final Context context, final SharedProjectReference projectReference, final String userEmail) {
        findUserWithEmailAddress(context, userEmail, projectReference);
    }

    public void findUserWithEmailAddress(final Context context, final String emailAddress, final SharedProjectReference sharedProjectReference) {
        db.collection(USERS_DB_PATH).whereEqualTo("email", emailAddress).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> users = queryDocumentSnapshots.toObjects(User.class);
                try {
                    User user = users.get(0);
                    addSharedProjectToUserPortfolio(context, sharedProjectReference.getProjectKey(), user.getUid());
                    addMemberDetailsToSharedProject(context, sharedProjectReference, emailAddress, user);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(context, "Failed to account associated with that email address", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toasty.error(context, "Oops! something went wrong, please try again").show();
            }
        });
    }

    private void addMemberDetailsToSharedProject(final Context context, final SharedProjectReference projectReference, final String userEmail, final User user) {
        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SharedProjectModel projectModel = documentSnapshot.toObject(SharedProjectModel.class);
                ArrayList<String> memberEmails = projectModel.getMemberEmails();
                ArrayList<String> memberNames = projectModel.getMemberNames();

                if (memberEmails == null) {
                    memberEmails = new ArrayList<>();
                }

                if (memberNames == null) {
                    memberNames = new ArrayList<>();
                }

                memberNames.add(user.getUserName());
                memberEmails.add(userEmail);
                projectModel.setMemberNames(memberNames);
                projectModel.setMemberEmails(memberEmails);
                updateSharedProjectInDb(context, projectModel, projectReference.getProjectKey());
            }
        });
    }

    public void saveSingleSharedProject(SharedProjectModel project) {
        db.document(SHARED_PROJECTS_DB_PATH + project.getProjectKey()).set(project).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Single Project Saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Single Project Failed");
            }
        });
    }

    public void exitFromSharedProject(final Context context, final SharedProjectReference projectReference) {

        // [1] Remove project from user projects
        db.document(USERS_DB_PATH + auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                ArrayList<SharedProjectReference> sharedProjects = user.getSharedProjectReferenceKeys();

                if (sharedProjects != null) {
                    if (sharedProjects.contains(projectReference)) {
                        sharedProjects.remove(projectReference);
                    }
                }

                user.setSharedProjectReferenceKeys(sharedProjects);
                updateUserInDB(user, context);
            }
        });

        //[2] Remove user_email from projects member_emails_list
        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SharedProjectModel sharedProject = documentSnapshot.toObject(SharedProjectModel.class);
                ArrayList<String> memberEmails = sharedProject.getMemberEmails();

                if (memberEmails != null) {
                    if (memberEmails.contains(auth.getCurrentUser().getEmail())) {
                        if (memberEmails.size() == 1) {
                            deleteSharedProjectFromDb(projectReference);
                        } else {
                            memberEmails.remove(auth.getCurrentUser().getEmail());
                            sharedProject.setMemberEmails(memberEmails);
                            updateSharedProjectInDb(context, sharedProject, projectReference.getProjectKey());
                        }
                    }
                }
            }
        });
    }

    private void deleteSharedProjectFromDb(SharedProjectReference projectReference) {
        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //successfully exited from project
            }
        });
    }
}
