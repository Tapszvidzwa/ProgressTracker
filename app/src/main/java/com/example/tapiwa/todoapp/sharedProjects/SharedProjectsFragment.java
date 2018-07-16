package com.example.tapiwa.todoapp.sharedProjects;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.DatabaseHandler;
import com.example.tapiwa.todoapp.home.MainActivty;
import com.example.tapiwa.todoapp.login.User;
import com.example.tapiwa.todoapp.sharedProjects.sharedProject.SharedProjectReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;


public class SharedProjectsFragment extends Fragment {

    private View sharedProjectsView;
    private FirebaseFirestore db;
    public static SharedProjectsAdapter mAdapter;
    public static ArrayList<SharedProjectReference> sharedProjectsList;
    private static DatabaseHandler remoteDb;
    private ListView sharedProjectsListV;

    public SharedProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedProjectsView = inflater.inflate(R.layout.fragment_shared_projects, container, false);
        initializeVariables();
        initializeViews();
        return sharedProjectsView;
    }

    private void initializeVariables() {
        db = FirebaseFirestore.getInstance();
        sharedProjectsList = new ArrayList<>();
        remoteDb = new DatabaseHandler();

    }

    private void initializeViews() {
        mAdapter = new SharedProjectsAdapter(getActivity().getApplicationContext(),
                R.layout.item_shared_project,
                sharedProjectsList);
        sharedProjectsListV = sharedProjectsView.findViewById(R.id.shared_projects_lstV);
        sharedProjectsListV.setAdapter(mAdapter);
        loadProjectsFromDb();
    }

    private void loadProjectsFromDb() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.document(USERS_DB_PATH + uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userCredentials = documentSnapshot.toObject(User.class);
                sharedProjectsList = userCredentials.getSharedProjectReferenceKeys();

                mAdapter = new SharedProjectsAdapter(getActivity().getApplicationContext(),
                        R.layout.item_shared_project,
                        sharedProjectsList);
                sharedProjectsListV.setAdapter(mAdapter);
            }
        });
    }

    public static void refreshProjects() {
        mAdapter.notifyDataSetChanged();
    }

    public static void addProject(final String projectName) {
                    remoteDb.addNewSharedProject(
                            MainActivty.activity.getApplicationContext(),
                            projectName);
                    mAdapter.notifyDataSetChanged();
    }
}