package com.example.tapiwa.todoapp.sharedProjects;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.DatabaseHandler;
import com.example.tapiwa.todoapp.login.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;


public class SharedProjectsFragment extends Fragment {

    private View sharedProjectsView;
    private FloatingActionButton addProjectFab;
    private ListView sharedProjectsListView;
    private FirebaseFirestore db;
    private SharedProjectsAdapter mAdapter;
    private ArrayList<String> sharedProjectsList;
    private DatabaseHandler remoteDb;

    public SharedProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedProjectsView = inflater.inflate(R.layout.fragment_shared_projects, container, false);
        addProjectFab = sharedProjectsView.findViewById(R.id.shared_projects_add_fab);
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
        addProjectFab = sharedProjectsView.findViewById(R.id.shared_projects_add_fab);
        sharedProjectsListView = sharedProjectsView.findViewById(R.id.shared_projects_lstV);
        mAdapter = new SharedProjectsAdapter(this.getActivity().getApplicationContext(), R.layout.item_shared_project, sharedProjectsList);
        loadSharedProjectsFromDb();
        setListeners();
    }

    private void setListeners() {
        addProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewProjectTitleDialogue();
            }
        });
    }

    private void loadSharedProjectsFromDb() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        db.document(USERS_DB_PATH + uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userCredentials = documentSnapshot.toObject(User.class);
                sharedProjectsList = userCredentials.getSharedProjectKeys();

                if(sharedProjectsList.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void getNewProjectTitleDialogue() {

        //Get title of new project
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_new_project));

        int maxLength = 200;
        final EditText givenTitle = new EditText(getActivity().getApplicationContext());
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (givenTitle.getText().toString().length() > 0) {

                    remoteDb.addNewSharedProject(
                                    getActivity().getApplicationContext(),
                                    givenTitle.getText().toString());

                    //Load projects from database again and refresh
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toasty.info(getActivity().getApplicationContext(), getString(R.string.no_task_entered), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}