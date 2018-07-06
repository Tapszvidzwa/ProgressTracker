package com.example.tapiwa.todoapp.personalProjects;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.personalProjects.personalProjectList.PersonalProjectListFragment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

;

public class PersonalProjectsFragment extends Fragment {

    public static ImageView restingDude;
    public static TextView noGoalsText;
    private ListView personalProjectsListV;
    private View personalProjectsView;
    private TextView percentageTxtV;
    private ArrayList<PersonalProjectModel> personalProjectsList;
    private PersonalProjectListModel personalProjectListModel;
    private PersonalProjectsAdapter adapter;
    private FloatingActionButton addProjectFab;
    private TextView personalProjectsTitle;

    public PersonalProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personalProjectsView = inflater.inflate(R.layout.personal_projects_fragment, container, false);
        initializeViews();
        initializeVariables();
        getPersonalProjects();
        return personalProjectsView;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.personal_projects_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.rename_project:
                getNewProjectTitleDialogue("renameProject", info.position);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.delete_project:
                deleteProject(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

     /*   PersonalProjectListModel projectsList = new PersonalProjectListModel();
        projectsList.setProjects(personalProjectsList);

        Gson gson = new Gson();
        String personalProjectsJson = gson.toJson(projectsList);

        FileOutputStream fos = null;
        try {
            File personalProjectsFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Personal_projects_file));
            personalProjectsFile.createNewFile();

            fos = new FileOutputStream(personalProjectsFile);
            byte[] personalProjectsFileBytes = personalProjectsJson.getBytes();
            fos.write(personalProjectsFileBytes);
            fos.flush();

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), getString(R.string.failed_file_creation), Toast.LENGTH_SHORT);
            return;
        } finally {

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } */
    }

    private void deleteProject(int pos) {
       personalProjectsList.remove(pos);
       adapter.notifyDataSetChanged();
    }

    private void initializeVariables() {
        personalProjectsList = new ArrayList<>();
    }

    private void getPersonalProjects() {

    /*    BufferedReader br = null;

        try {
            File personalProjectsFiles = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Personal_projects_file));
            personalProjectsFiles.createNewFile();

            br = new BufferedReader(new FileReader(personalProjectsFiles));

            Gson gson = new Gson();

            personalProjectListModel = gson.fromJson(br, PersonalProjectListModel.class);

            if (personalProjectListModel != null) {
                personalProjectsList = personalProjectListModel.getProjects();
                adapter = new PersonalProjectsAdapter(getActivity().getApplicationContext(), R.layout.item_project, personalProjectsList);
                personalProjectsListV.setAdapter(adapter);
            }

        } catch (IOException e) {
            //TODO: handle appropriately
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                //TODO: handle appropriately
            }
        } */

    }

    private void initializeViews() {
     /*   personalProjectsListV = personalProjectsView.findViewById(R.id.personal_projects_lstV);
        addProjectFab = personalProjectsView.findViewById(R.id.personal_projects_add_fab);
        adapter = new PersonalProjectsAdapter(getActivity().getApplicationContext(), R.layout.item_project, personalProjectsList);
        registerForContextMenu(personalProjectsListV);

        addProjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewProjectTitleDialogue("newProject", -1);
            }
        });

        personalProjectsListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedProjectModel chosenProject = personalProjectsList.get(i);

                Bundle bundle = new Bundle();
                bundle.putString("projectName", chosenProject.getProjectTitle());
                bundle.putSerializable("taskList", chosenProject.getProjectTask());
                bundle.putSerializable("allProjects", personalProjectListModel);
                android.app.Fragment projectTodoFragment = new PersonalProjectListFragment();
                projectTodoFragment.setArguments(bundle);

                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container_holder, projectTodoFragment).commit();
            }
        }); */

    }

    public void getNewProjectTitleDialogue(final String requestType, final int pos) {

     /*   //Get title of new project
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

                    if(requestType.equals("renameProject")) {
                        //Rename project
                        SharedProjectModel projectModel = new SharedProjectModel();
                        projectModel = personalProjectsList.get(pos);
                        projectModel.setProjectTitle(givenTitle.getText().toString());
                        personalProjectsList.set(pos, projectModel);
                        adapter.notifyDataSetChanged();
                    } else {
                        //Create a new project
                        SharedProjectModel newProject = new SharedProjectModel();
                        newProject.setProjectTitle(givenTitle.getText().toString());
                        newProject.setProjectTask(null);
                        personalProjectsList.add(newProject);
                        adapter.notifyDataSetChanged();
                    }

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

        builder.show(); */
    }

}
