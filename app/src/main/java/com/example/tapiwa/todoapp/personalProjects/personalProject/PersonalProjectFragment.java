package com.example.tapiwa.todoapp.personalProjects.personalProject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PersonalProjectFragment extends androidx.fragment.app.Fragment {

    public static ImageView restingDude;
    public static TextView noGoalsText, date;
    private static ArrayList<PersonalProjectTask> tasksList;
    private static PersonalProjectAdapter adapter;
    private ListView tasksListV;
    private View personalProjectsPageView;
    private TextView percentageTxtV;
    private String CURRENT_DATE;
    private FileHandler fileHandler;
    private PersonalProjectsContainerModel personalProjectsContainerModel;
    private PersonalProjectModel personalProjectModel;
    public static int clickedProject = 0;
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
        return personalProjectsPageView;
    }

    @Override
    public void onResume() {
        retrieveSavedTasks(PROJECT_KEY);
        setPercentage();
        super.onResume();
    }

    @Override
    public void onPause() {
        saveTasks();
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.personal_project_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        clickedProject = info.position;

        switch (item.getItemId()) {
            case R.id.rename_task:
                MainActivity.inputRequest.setInputRequest(InputRequests.InputRequestType.RENAME_PROJECT);
                MainActivity.getInputForFragment(MainActivity.visibleFragment, tasksList.get(clickedProject).getTask());
                return true;
            case R.id.delete_task:
                deleteTask(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setPercentage() {
        percentageTxtV.setTextColor(Color.rgb(208, 35, 35));
        percentageTxtV.setText(personalProjectModel.getPercentageCompleted() + "%");
    }

    private void deleteTask(int pos) {
        tasksList.remove(pos);
        adapter.notifyDataSetChanged();
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
            if (personalProjectModel.getProjectTasks() != null && personalProjectModel != null) {
                tasksList = personalProjectModel.getProjectTasks();
            } else {
                tasksList = new ArrayList<>();
            }
            adapter = new PersonalProjectAdapter(getActivity().getApplicationContext(),
                    R.layout.item_goal_list,
                    tasksList);
            tasksListV.setAdapter(adapter);
        } catch (NullPointerException e) {
            //no - op
        }
    }

    private void saveTasks() {
        personalProjectModel.setProjectTasks(tasksList);
        personalProjectModel.setPercentageCompleted();
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
        tasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }

    public static void renameTask(String newTitle) {
        PersonalProjectTask task = tasksList.get(clickedProject);
        task.setTask(newTitle);
        task.setDateLastModified(Long.toString(System.currentTimeMillis()));
        tasksList.set(clickedProject, task);
        adapter.notifyDataSetChanged();
    }

    private void initializeVariables() {
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        Bundle args = getArguments();
        PROJECT_KEY = args.getString("projectKey");
        fileHandler = new FileHandler(getActivity().getApplicationContext());
        tasksList = new ArrayList<>();
    }

    private void initializeViews() {
        percentageTxtV = personalProjectsPageView.findViewById(R.id.percentage_completed);
        restingDude = personalProjectsPageView.findViewById(R.id.resting_dude);
        noGoalsText = personalProjectsPageView.findViewById(R.id.no_goals_text);
        date = personalProjectsPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        tasksListV = personalProjectsPageView.findViewById(R.id.goals_lstV);
        registerForContextMenu(tasksListV);
        setClickListeners();
    }

    private void setClickListeners() {
        tasksListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PersonalProjectTask updatedTask = tasksList.get(i);

                if (!updatedTask.getCompletionStatus().equals("completed")) {
                    updatedTask.setCompletionStatus("completed");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText("Congratulations!").setContentText("Congratulations on finishing all your tasks");
                        dg.show();
                    }

                } else {
                    updatedTask.setCompletionStatus("uncompleted");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                }
                calculatePercentage();
            }
        });

        tasksListV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });
    }

    private void calculatePercentage() {
        percentageTxtV.setTextColor(Color.rgb(208, 35, 35));
        if (tasksList != null) {
            if (tasksList.size() == 0) {
                percentageTxtV.setText(Integer.toString(0) + "%");
                return;
            } else {
                int completedTasks = countCompletedTasks();
                double result = ((double) completedTasks / tasksList.size()) * 100;
                DecimalFormat numFormat = new DecimalFormat("#");
                String newPercentage = numFormat.format(result) + "%";
                percentageTxtV.setText(newPercentage);
                checkTasksCompletion();
                return;
            }
        }
    }

    private boolean checkTasksCompletion() {
        Iterator iter = tasksList.iterator();
        while (iter.hasNext()) {
            PersonalProjectTask task = (PersonalProjectTask) iter.next();
            if (task.getCompletionStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private int countCompletedTasks() {
        Iterator iter = tasksList.iterator();
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
