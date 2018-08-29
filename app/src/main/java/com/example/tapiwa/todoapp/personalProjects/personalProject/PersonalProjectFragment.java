package com.example.tapiwa.todoapp.personalProjects.personalProject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PersonalProjectFragment extends androidx.fragment.app.Fragment {

    public static ImageView restingDude;
    public static TextView noGoalsText, date;
    private static ArrayList<PersonalProjectTask> personalProjectTasksList;
    private static PersonalProjectAdapter adapter;
    private ListView tasksListV;
    private View personalProjectsPageView;
    private TextView percentageTxtV;
    private String CURRENT_DATE;
    private FileHandler fileHandler;
    private PersonalProjectsContainerModel personalProjectsContainerModel;
    private PersonalProjectModel personalProjectModel;
    String PROJECT_KEY;

    public PersonalProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        personalProjectsPageView = inflater.inflate(R.layout.fragment_personal_project, container, false);
        initializeVariables();
        initializeViews();
        calculatePercentage();
        return personalProjectsPageView;
    }

    @Override
    public void onResume() {
        retrieveSavedTasks(PROJECT_KEY);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveTasks();
    }

    private void retrieveSavedTasks(String projectKey) {
        JSONObject personalProjectsJson = fileHandler.readFile(getString(R.string.PERSONAL_PROJECTS_FILE));
        if (personalProjectsJson != null) {
            Gson gson = new Gson();
            personalProjectsContainerModel = gson.fromJson(personalProjectsJson.toString(),
                    PersonalProjectsContainerModel.class);
        }
        personalProjectModel = personalProjectsContainerModel.getPersonalProject(projectKey);
        populateTasksList();
        MainActivity.toolbar.setTitle(personalProjectModel.getProjectTitle());
    }

    private void populateTasksList() {
        try {
            if(personalProjectModel.getProjectTasks() != null && personalProjectModel != null) {
                personalProjectTasksList = personalProjectModel.getProjectTasks();
            } else {
                personalProjectTasksList = new ArrayList<>();
            }
            adapter = new PersonalProjectAdapter(getActivity().getApplicationContext(),
                    R.layout.item_goal_list,
                    personalProjectTasksList);
            tasksListV.setAdapter(adapter);
        } catch (NullPointerException e) {
            //no - op
        }
    }

    private void saveTasks() {
        personalProjectModel.setProjectTasks(personalProjectTasksList);
        personalProjectsContainerModel.updateProject(personalProjectModel);
        String personalProjectsJson = convertPersonalProjectsContainerToJson();
        fileHandler.saveFile(getString(R.string.PERSONAL_PROJECTS_FILE), personalProjectsJson);
    }

    private String convertPersonalProjectsContainerToJson() {
        Gson gson = new Gson();
        return gson.toJson(personalProjectsContainerModel);
    }

    public static void addNewTask(final String task) {
        PersonalProjectTask newTask = new PersonalProjectTask();
        newTask.setTask(task);
        newTask.setDefaultCompletionStatus();
        newTask.setDateLastModified(Long.toString(System.currentTimeMillis()));
        personalProjectTasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }

    private void initializeVariables() {
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        Bundle args = getArguments();
        PROJECT_KEY = args.getString("projectKey");
        fileHandler = new FileHandler(getActivity().getApplicationContext());
        personalProjectTasksList = new ArrayList<>();
    }

    private void initializeViews() {
        percentageTxtV = personalProjectsPageView.findViewById(R.id.percentage_completed);
        restingDude = personalProjectsPageView.findViewById(R.id.resting_dude);
        noGoalsText = personalProjectsPageView.findViewById(R.id.no_goals_text);
        date = personalProjectsPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        tasksListV = personalProjectsPageView.findViewById(R.id.goals_lstV);
        setClickListeners();
    }

    private void setClickListeners() {
        tasksListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PersonalProjectTask updatedTask = personalProjectTasksList.get(i);

                if (!updatedTask.getCompletionStatus().equals("completed")) {
                    updatedTask.setCompletionStatus("completed");
                    personalProjectTasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText("Congratulations!").setContentText("Congratulations on finishing all your tasks");
                        dg.show();
                        personalProjectTasksList.clear();
                    }

                } else {
                    updatedTask.setCompletionStatus("uncompleted");
                    personalProjectTasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                }
                calculatePercentage();
            }
        });
    }

    private void calculatePercentage() {
        percentageTxtV.setTextColor(Color.rgb(208, 35, 35));
        if (personalProjectTasksList != null) {
            if (personalProjectTasksList.size() == 0) {
                percentageTxtV.setText(Integer.toString(0) + "%");
                return;
            } else {
                int completedTasks = countCompletedTasks();
                int percentage = (int) Math.floor(((double) completedTasks / personalProjectTasksList.size()) * 100);
                percentageTxtV.setText(Integer.toString(percentage) + "%");
                checkTasksCompletion();
                return;
            }
        }
    }

    private boolean checkTasksCompletion() {
        Iterator iter = personalProjectTasksList.iterator();
        while (iter.hasNext()) {
            PersonalProjectTask task = (PersonalProjectTask) iter.next();
            if (task.getCompletionStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private int countCompletedTasks() {
        Iterator iter = personalProjectTasksList.iterator();
        int i = 0;

        while (iter.hasNext()) {
            PersonalProjectTask task = (PersonalProjectTask) iter.next();
            if (task.getCompletionStatus().equals("completed")) {
                ++i;
            }
        }
        return i;
    }
}
