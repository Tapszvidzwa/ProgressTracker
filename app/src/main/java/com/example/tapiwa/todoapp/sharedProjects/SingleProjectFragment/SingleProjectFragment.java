package com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.DatabaseHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectReference;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.projectmembers.ProjectMembersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static com.example.tapiwa.todoapp.Utils.Constants.SHARED_PROJECTS_DB_PATH;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.SINGLE_SHARED_PROJECT;

public class SingleProjectFragment extends androidx.fragment.app.Fragment {

    public static SingleProjectAdapter mAdapter;
    public static ArrayList<SharedProjectTask> tasksList;
    public static SharedProjectModel sharedProjectModel;
    private static View sharedProjectsView;
    private static FirebaseFirestore db;
    private static DatabaseHandler remoteDb;
    private static ListView sharedProjectsListV;
    private static Activity activity;
    private static SharedProjectReference projectReference;
    private FirebaseUser user;
    private TextView percentageCompleted;
    public static int clickedProject;
    public static String TAG, username;

    public SingleProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedProjectsView = inflater.inflate(R.layout.fragment_single_project, container, false);
        initializeVariables();
        initializeViews();
        initializeListeners();
        setHasOptionsMenu(true);
        return sharedProjectsView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProjectsFromDb();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.single_shared_project_item, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.edit_task:
                clickedProject = info.position;
                MainActivity.inputRequest.setInputRequest(InputRequests.InputRequestType.RENAME_TASK);
                MainActivity.getInputForFragment(MainActivity.visibleFragment);
                return true;
            case R.id.delete_task:
                deleteTask(info.position);
                return true;
            case R.id.exit_project:
                exitProject();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static void loadProjectsFromDb() {
        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                sharedProjectModel = documentSnapshot.toObject(SharedProjectModel.class);
                try {
                    if (sharedProjectModel != null) {
                        tasksList = sharedProjectModel.getProjectTasks();
                        setTitle();
                    }
                    if (tasksList != null) {
                        mAdapter = new SingleProjectAdapter(activity.getApplicationContext(),
                                R.layout.item_single_shared_project,
                                tasksList);
                        sharedProjectsListV.setAdapter(mAdapter);
                    }
                } catch (NullPointerException ex) {
                    Log.d(TAG, "Something went wrong in populating the task list");
                }
            }
        });
    }

    private static void setTitle() {
        MainActivity.toolbar.setTitle(sharedProjectModel.getProjectTitle());
    }

    public static void addTask(final String taskTitle) {
        SharedProjectTask task = new SharedProjectTask();
        task.setCompletionStatus("uncompleted");
        task.setDateLastModified(Long.toString(System.currentTimeMillis()));
        task.setWhoLastModifiedTask(username);
        task.setTask(taskTitle);
        remoteDb.addSharedProjectTaskToDb(activity.getApplicationContext(), task, projectReference.getProjectKey());
    }

    private void initializeVariables() {
        MainActivity.visibleFragment = SINGLE_SHARED_PROJECT;
        Bundle args = getArguments();
        projectReference = (SharedProjectReference) args.getSerializable("projectReference");
        db = FirebaseFirestore.getInstance();
        tasksList = new ArrayList<>();
        remoteDb = new DatabaseHandler();
        activity = getActivity();
        user = FirebaseAuth.getInstance().getCurrentUser();
        username = user.getDisplayName();
    }

    private void initializeViews() {
        sharedProjectsListV = sharedProjectsView.findViewById(R.id.single_project_listV);
        percentageCompleted = sharedProjectsView.findViewById(R.id.single_project_percentage_completed);
        percentageCompleted.setTextColor(Color.rgb(208, 35, 35));
        registerForContextMenu(sharedProjectsListV);
        loadProjectsFromDb();
    }

    private void initializeListeners() {
        sharedProjectsListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateTask(i);
            }
        });

        db.document(SHARED_PROJECTS_DB_PATH + projectReference.getProjectKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        sharedProjectModel = documentSnapshot.toObject(SharedProjectModel.class);

                        if (tasksList != null) {
                            tasksList.clear();
                        }

                        try {
                            tasksList = sharedProjectModel.getProjectTasks();
                            mAdapter = new SingleProjectAdapter(activity.getApplicationContext(),
                                    R.layout.item_shared_project,
                                    tasksList);
                            percentageCompleted.setText(sharedProjectModel.getPercentageCompleted());
                        } catch (NullPointerException ex) {
                            // no - op
                        }
                    }
                });
    }

    private void updateTask(int i) {
        SharedProjectTask clickedTask = tasksList.get(i);

        if (clickedTask.getCompletionStatus().equals("uncompleted")) {
            clickedTask.setCompletionStatus("completed");
            clickedTask.setWhoLastModifiedTask(user.getDisplayName());
            clickedTask.setDateLastModified(Long.toString(System.currentTimeMillis()));
            tasksList.set(i, clickedTask);
        } else {
            clickedTask.setCompletionStatus("uncompleted");
            clickedTask.setWhoLastModifiedTask(username);
            clickedTask.setDateLastModified(Long.toString(System.currentTimeMillis()));
            tasksList.set(i, clickedTask);
        }
        saveProject();
        mAdapter.notifyDataSetChanged();
    }

    public static void addMember(String userEmail) {
        remoteDb.addMemberToSharedProject(activity.getApplicationContext(), projectReference, userEmail);
    }

    public static void viewSharedProjectMembers() {
            Intent intent = new Intent(MainActivity.activity, ProjectMembersActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("projectModel", sharedProjectModel);
            intent.putExtras(bundle);
            MainActivity.activity.startActivity(intent);
    }

    public static void saveProject() {
        sharedProjectModel.setProjectTasks(tasksList);
        sharedProjectModel.setPercentageCompleted();
        remoteDb.saveSingleSharedProject(sharedProjectModel);
    }

    private void exitProject() {
        remoteDb.exitFromSharedProject(getContext(), projectReference);
        MainActivity.switchToFragment(MainActivity.FragmentName.SHARED_PROJECTS, null);
    }

    private void deleteTask(int pos) {
        tasksList.remove(pos);
        mAdapter.notifyDataSetChanged();
        saveProject();
    }

    public static void renameTask(String taskTitle) {
        SharedProjectTask task = tasksList.get(clickedProject);
        task.setTask(taskTitle);
        tasksList.set(clickedProject, task);
        sharedProjectModel.setProjectTasks(tasksList);
        remoteDb.updateSharedProjectInDb(activity.getApplicationContext(), sharedProjectModel, projectReference.getProjectKey());
    }
}