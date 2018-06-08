package com.example.tapiwa.todoapp;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class ProjectsFragment extends Fragment {

    private View projectsPageView;
    private LinkedList<TaskList> personalProjects;
    private LinkedList<TaskList> sharedProjects;
    private FloatingActionButton addProjectFab;
    private ProjectsAdapter adapter, sharedProjectsAdapter;
    private ListView personalProjectsListView, sharedProjectsListView;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        projectsPageView = inflater.inflate(R.layout.projects, container, false);
        personalProjects = new LinkedList<>();
        sharedProjects = new LinkedList<>();
        initializeViews();
        getProjects();
        return projectsPageView;
    }

    private void initializeViews() {

        addProjectFab = projectsPageView.findViewById(R.id.add_project);
        personalProjectsListView = projectsPageView.findViewById(R.id.personal_projects);
        sharedProjectsListView = projectsPageView.findViewById(R.id.shared_projects);

        addProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProject();
            }
        });

    }

    private void getProjects() {
        try {
            //open tasks file
            File personal_projects = new File(getActivity().getApplicationContext().getFilesDir(), "personal_projects.json");
            File shared_projects = new File(getActivity().getApplicationContext().getFilesDir(), "shared_projects.json");
            //create new file if the file does not exist
            personal_projects.createNewFile();
            personal_projects.createNewFile();

            BufferedReader br_personal_projects = new BufferedReader(new FileReader(personal_projects));
            BufferedReader br_shared_projects = new BufferedReader(new FileReader(personal_projects));

            Gson gson = new Gson();

            ProjectsList personalProjectsList = gson.fromJson(br_personal_projects, ProjectsList.class);
            ProjectsList sharedProjectsList = gson.fromJson(br_shared_projects, ProjectsList.class);

            if(personalProjectsList != null) {
                personalProjects = personalProjectsList.getTaskList();
                adapter = new ProjectsAdapter(getActivity().getApplicationContext(),R.layout.item_project, personalProjectsList.getTaskList());
                // goalsList.setAdapter(adapter);
            }

            if(sharedProjectsList != null) {
                sharedProjects = sharedProjectsList.getTaskList();
                sharedProjectsAdapter = new ProjectsAdapter(getActivity().getApplicationContext(),R.layout.item_project, sharedProjectsList.getTaskList());
                sharedProjectsListView.setAdapter(adapter);
            }

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    public void addNewProject() {
        CharSequence projectType[] = new CharSequence[] {"Personal Project", "Shared Project"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Project Type");
        builder.setItems(projectType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {

                if(pos == 0) {
                 //create a personal project
                    createProject("Personal Project");
                } else {
                    //create a shared project
                    createProject("Shared Project");
                }

            }
        });
        builder.show();
    }

    public void createProject(final String projectType) {

        //Get title of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle("Enter Project Name");

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

                if(givenTitle.getText().toString().length() > 0) {

                    //create the new task
                    TaskList taskList = new TaskList();
                    taskList.setName(givenTitle.getText().toString().trim());
               
                    if(projectType.equals("Personal Project")) {
                        personalProjects.add(taskList);
                      //  adapter.notifyDataSetChanged();
                    } else {
                        sharedProjects.add(taskList);
                        sharedProjectsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toasty.info(getActivity().getApplicationContext(), "Please provide a task description", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume(){
        super.onResume();
        getProjects();
    }

    @Override
    public void onPause() {
        super.onPause();

        ProjectsList pProjectlist = new ProjectsList();
        ProjectsList sProjectsList = new ProjectsList();
        pProjectlist.setTaskList(personalProjects);
        sProjectsList.setTaskList(sharedProjects);

        Gson gson = new Gson();
        String tasksJson = gson.toJson(pProjectlist);
        String tasksJson2 = gson.toJson(sProjectsList);

        FileOutputStream fos = null;

        try {
            //open tasks file
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), "personal_projects.json");
            File tasksFile2 = new File(getActivity().getApplicationContext().getFilesDir(), "shared_projects.json");
            //create new file if the file does not exist
            tasksFile.createNewFile();
            tasksFile2.createNewFile();

            //save/write the tasks to the personal file
            fos = new FileOutputStream(tasksFile);
            byte[] tasksFileBytes = tasksJson.getBytes();
            fos.write(tasksFileBytes);
            fos.flush();

            //save/write the tasks to the shared file
            fos = new FileOutputStream(tasksFile2);
            byte[] tasksFileBytes2 = tasksJson2.getBytes();
            fos.write(tasksFileBytes2);
            fos.flush();

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT);
            return;
        } finally {

            try {
                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
