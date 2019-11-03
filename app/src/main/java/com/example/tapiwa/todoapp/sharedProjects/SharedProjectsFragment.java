package com.example.tapiwa.todoapp.sharedProjects;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.utils.DatabaseHandler;
import com.example.tapiwa.todoapp.utils.InputRequests;
import com.example.tapiwa.todoapp.navigation.NavigationController;
import com.example.tapiwa.todoapp.login.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static com.example.tapiwa.todoapp.fragmentFactory.FragmentName.SINGLE_GROUP_PROJECT;
import static com.example.tapiwa.todoapp.utils.Constants.USERS_DB_PATH;


public class SharedProjectsFragment extends androidx.fragment.app.Fragment {

    private static View sharedProjectsView;
    private static FirebaseFirestore db;
    public static SharedProjectsAdapter mAdapter;
    public static ArrayList<SharedProjectReference> sharedProjectsList;
    private static DatabaseHandler remoteDb;
    private static ListView sharedProjectsListV;
    public static Activity activity;
    public static int clickedProject;
    private ContextMenu menu;

    public SharedProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedProjectsView = inflater.inflate(R.layout.fragment_shared_projects, container, false);
        initializeVariables();
        initializeViews();
        initializeListeners();
        return sharedProjectsView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.shared_projects_menu, menu);
        this.menu = menu;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        clickedProject = info.position;

        switch (item.getItemId()) {
            case R.id.rename_project:
                NavigationController.inputRequest.setInputRequest(InputRequests.InputRequestType.RENAME_PROJECT);
                NavigationController.getInputForFragment(NavigationController.visibleFragment, "");
                return true;
            case R.id.exit_project:
                exitFromProject();
                break;
            case R.id.delete_project:
                deleteProject();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void initializeVariables() {
        db = FirebaseFirestore.getInstance();
        sharedProjectsList = new ArrayList<>();
        remoteDb = new DatabaseHandler();
        activity = getActivity();
    }

    private void initializeViews() {
        sharedProjectsListV = sharedProjectsView.findViewById(R.id.shared_projects_lstV);
        mAdapter = new SharedProjectsAdapter(getActivity().getApplicationContext(),
                R.layout.item_shared_project,
                sharedProjectsList);
        registerForContextMenu(sharedProjectsListV);
        sharedProjectsListV.setAdapter(mAdapter);
        loadProjectsFromDb();
    }

    private void initializeListeners() {
        sharedProjectsListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedProjectReference projectReference = sharedProjectsList.get(i);
                Bundle bundle = new Bundle();
                bundle.putSerializable("projectReference", projectReference);
                NavigationController.switchToFragment(SINGLE_GROUP_PROJECT, bundle);
            }
        });
    }

    private void deleteProject() {
        //TODO: implement delete project
    }

    private static void loadProjectsFromDb() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.document(USERS_DB_PATH + uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                User userCredentials = documentSnapshot.toObject(User.class);

                try {
                    sharedProjectsList.clear();
                    sharedProjectsList = userCredentials.getSharedProjectReferenceKeys();
                    mAdapter = new SharedProjectsAdapter(activity.getApplicationContext(),
                            R.layout.item_shared_project,
                            sharedProjectsList);
                    sharedProjectsListV.setAdapter(mAdapter);
                } catch (NullPointerException ex) {

                }
            }
        });
    }

    public static void addProject(final String projectName) {
        remoteDb.addNewSharedProject(
                NavigationController.activity.getApplicationContext(),
                projectName);
        loadProjectsFromDb();
        mAdapter.notifyDataSetChanged();
    }

    public static void renameProject(String projectName) {
        SharedProjectReference projectReference = sharedProjectsList.get(clickedProject);
        remoteDb.renameSharedProject(activity.getApplicationContext(), projectName, projectReference);
    }

    private void exitFromProject() {
        SharedProjectReference projectReference = sharedProjectsList.get(clickedProject);
        remoteDb.exitFromSharedProject(getContext(), projectReference);
    }
}