package com.example.tapiwa.todoapp.personalProjects;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.home.MainActivty;
import com.example.tapiwa.todoapp.personalProjects.personalProjectList.PersonalProjectListFragment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

;import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.personalProjects.PersonalProjectsFragment.InputRequestType.RENAME_PROJECT;

public class PersonalProjectsFragment extends Fragment {

    private View personalProjectsView;
    private static ArrayList<PersonalProjectModel> personalProjectsList;
    private PersonalProjectListModel personalProjectListModel;
    private static PersonalProjectsAdapter adapter;
    private TextView personalProjectsTitle;
    private ListView personalProjectsListV;
    public static int clickedProject;
    public static InputRequestType inputRequestType;

    public PersonalProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personalProjectsView = inflater.inflate(R.layout.fragment_personal_projects, container, false);
        initializeViews();
        initializeVariables();
        getProjects();
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
        clickedProject = info.position;

        switch (item.getItemId()) {
            case R.id.rename_project:
                inputRequestType = RENAME_PROJECT;
                MainActivty.getInputForFragment(MainActivty.visibleFragment);
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

        PersonalProjectListModel projectsList = new PersonalProjectListModel();
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

        }
    }

    private void deleteProject(int pos) {
        personalProjectsList.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private void initializeVariables() {
        personalProjectsList = new ArrayList<>();
        inputRequestType = InputRequestType.NONE;
        clickedProject = 0;
    }

    private void getProjects() {

        BufferedReader br = null;
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
        }
    }

    private void initializeViews() {
        personalProjectsListV = personalProjectsView.findViewById(R.id.personal_projects_lstV);
        adapter = new PersonalProjectsAdapter(getActivity().getApplicationContext(), R.layout.item_project, personalProjectsList);
        registerForContextMenu(personalProjectsListV);

        personalProjectsListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openProject(personalProjectsList.get(i));
            }
        });
    }

    public static void addProject(final String projectName) {
        PersonalProjectModel newProject = new PersonalProjectModel();
        newProject.setProjectTitle(projectName);
        personalProjectsList.add(newProject);
        adapter.notifyDataSetChanged();
    }

    private void openProject(PersonalProjectModel chosenProject) {
        Bundle bundle = new Bundle();
        bundle.putString("projectName", chosenProject.getProjectTitle());
        bundle.putSerializable("taskList", chosenProject.getProjectTasks());
        bundle.putSerializable("allProjects", personalProjectListModel);
        android.app.Fragment projectTodoFragment = new PersonalProjectListFragment();
        projectTodoFragment.setArguments(bundle);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container_holder, projectTodoFragment).commit();
    }

    public static void renameProject(final String projectName) {
        PersonalProjectModel projectModel = new PersonalProjectModel();
        projectModel = personalProjectsList.get(clickedProject);
        projectModel.setProjectTitle(projectName);
        personalProjectsList.set(clickedProject, projectModel);
        adapter.notifyDataSetChanged();
    }

    public enum InputRequestType {
        RENAME_PROJECT, CREATE_NEW_PROJECT, NONE;
    }

}
