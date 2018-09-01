package com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer;

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
import com.example.tapiwa.todoapp.Utils.Constants;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.LinkedList;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.PERSONAL_PROJECT;

public class PersonalProjectsContainerFragment extends androidx.fragment.app.Fragment {

    public static int clickedProject;
    private static LinkedList<PersonalProjectModel> personalProjectsList;
    private static PersonalProjectsContainerAdapter adapter;
    private View personalProjectsView;
    private PersonalProjectsContainerModel personalProjectsContainerModel;
    private ListView personalProjectsListV;
    private FileHandler fileHandler;

    public PersonalProjectsContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personalProjectsView = inflater.inflate(R.layout.fragment_personal_projects_container, container, false);
        initializeVariables();
        initializeViews();
        return personalProjectsView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.personal_projects_container_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        clickedProject = info.position;

        switch (item.getItemId()) {
            case R.id.rename_project:
                MainActivity.getInputForFragment(MainActivity.visibleFragment);
                return true;
            case R.id.delete_project:
                deleteProject(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveSavedTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveTasks();
    }

    public static void addProject(final String projectName) {
        PersonalProjectModel newProject = new PersonalProjectModel();
        newProject.setProjectTitle(projectName);
        newProject.setLastModifiedtime(Long.toString(System.currentTimeMillis()));
        newProject.setProjectKey();

        if (personalProjectsList == null) {
            personalProjectsList = new LinkedList<>();
        }
        personalProjectsList.add(newProject);
        adapter.notifyDataSetChanged();
    }

    public static void renameProject(final String projectName) {
        PersonalProjectModel projectModel = personalProjectsList.get(clickedProject);
        projectModel.setProjectTitle(projectName);
        personalProjectsList.set(clickedProject, projectModel);
        adapter.notifyDataSetChanged();
    }

    private void initializeVariables() {
        personalProjectsList = new LinkedList<>();
        clickedProject = 0;
        fileHandler = new FileHandler(getContext());
    }

    private void initializeViews() {
        personalProjectsListV = personalProjectsView.findViewById(R.id.personal_projects_lstV);
        adapter = new PersonalProjectsContainerAdapter(getActivity().getApplicationContext(), R.layout.item_project, personalProjectsList);
        registerForContextMenu(personalProjectsListV);
        personalProjectsListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openProject(personalProjectsList.get(i));
            }
        });
    }

    private void saveTasks() {
        String tasksJson = convertTasksListToJsonString();
        fileHandler.saveFile(getString(R.string.PERSONAL_PROJECTS_FILE), tasksJson);
    }

    private void retrieveSavedTasks() {
        JSONObject tasksJson = fileHandler.readFile(getString(R.string.PERSONAL_PROJECTS_FILE));
        populateTaskList(tasksJson);
    }

    private void populateTaskList(JSONObject tasksJson) {
        if (tasksJson != null) {
            Gson gson = new Gson();
            personalProjectsContainerModel = gson.fromJson(tasksJson.toString(),
                    PersonalProjectsContainerModel.class);
            try {
                if(personalProjectsContainerModel.getProjects() != null) {
                    personalProjectsList = personalProjectsContainerModel.getProjects();
                } else {
                    personalProjectsList = new LinkedList<>();
                }

                adapter = new PersonalProjectsContainerAdapter(getActivity().getApplicationContext(),
                        R.layout.item_project,
                        personalProjectsList);
                personalProjectsListV.setAdapter(adapter);
            } catch (NullPointerException e) {
                // no - op
            }
        }
    }

    private String convertTasksListToJsonString() {
        Gson gson = new Gson();
        PersonalProjectsContainerModel personalProjectsModel = new PersonalProjectsContainerModel();
        personalProjectsModel.setProjects(personalProjectsList);
        return gson.toJson(personalProjectsModel);
    }

    private void deleteProject(int pos) {
        personalProjectsList.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private void openProject(PersonalProjectModel chosenProject) {
        Bundle bundle = new Bundle();
        bundle.putString("projectKey", chosenProject.getProjectKey());
        MainActivity.switchToFragment(PERSONAL_PROJECT, bundle);
    }
}
