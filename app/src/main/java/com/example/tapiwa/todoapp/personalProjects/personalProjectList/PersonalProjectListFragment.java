package com.example.tapiwa.todoapp.personalProjects.personalProjectList;

import android.app.Fragment;
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
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectListModel;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PersonalProjectListFragment extends Fragment {

    private ListView goalsList;
    public static ImageView restingDude;
    public static TextView noGoalsText, date;
    private View personalProjectsPageView;
    private TextView percentageTxtV;
    private static ArrayList<Task> personalProjectTasksList;
    private static PersonalProjectsListAdapter adapter;
    private FloatingActionButton addTask;
    private PersonalProjectListModel allProjectsList;
    private String CURRENT_DATE;
    private String PROJECT_NAME;
    private FileHandler fileHandler;

    public PersonalProjectListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        personalProjectsPageView = inflater.inflate(R.layout.personal_project_todo_list_fragment, container, false);
        initializeVariables();
        initializeViews();
        calculatePercentage();
        return personalProjectsPageView;
    }

    private void initializeVariables() {
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        Bundle args = getArguments();
        PROJECT_NAME = args.getString("projectName");
        allProjectsList = (PersonalProjectListModel) args.getSerializable("allProjects");
        // personalProjectTasksList = allProjectsList.getProject(PROJECT_NAME).getProjectTask();
        fileHandler = new FileHandler(getActivity().getApplicationContext());
    }

    private void initializeViews() {
        percentageTxtV = personalProjectsPageView.findViewById(R.id.percentage_completed);
        restingDude = personalProjectsPageView.findViewById(R.id.resting_dude);
        noGoalsText = personalProjectsPageView.findViewById(R.id.no_goals_text);
        date = personalProjectsPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        goalsList = personalProjectsPageView.findViewById(R.id.goals_lstV);
        adapter = new PersonalProjectsListAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, personalProjectTasksList);

        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task updatedTask = personalProjectTasksList.get(i);

                if (!updatedTask.getStatus().equals("completed")) {
                    updatedTask.setStatus("completed");
                    personalProjectTasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText("Congratulations!").setContentText("Congratulations on finishing all your tasks");
                        dg.show();
                        personalProjectTasksList.clear();
                    }

                } else {
                    updatedTask.setStatus("uncompleted");
                    personalProjectTasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                }

                calculatePercentage();
            }
        });

        adapter = new PersonalProjectsListAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, personalProjectTasksList);
        goalsList.setAdapter(adapter);
    }

    private void calculatePercentage() {
        percentageTxtV.setTextColor(Color.rgb(208, 35, 35));

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

    private boolean checkTasksCompletion() {
        Iterator iter = personalProjectTasksList.iterator();
        while (iter.hasNext()) {
            Task task = (Task) iter.next();
            if (task.getStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private int countCompletedTasks() {
        Iterator iter = personalProjectTasksList.iterator();
        int i = 0;

        while (iter.hasNext()) {
            Task task = (Task) iter.next();
            if (task.getStatus().equals("completed")) {
                ++i;
            }
        }

        return i;
    }

    public static void addNewTask(final String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");

        personalProjectTasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        PersonalProjectModel currentProject = new PersonalProjectModel();
        currentProject.setProjectTitle(PROJECT_NAME);
        //  currentProject.setProjectTask(personalProjectTasksList);

        allProjectsList.updateProject(currentProject);
        Gson gson = new Gson();
        String personalProjectsJson = gson.toJson(allProjectsList);
        fileHandler.saveFile(getString(R.string.Personal_projects_file), personalProjectsJson);
    }
}
