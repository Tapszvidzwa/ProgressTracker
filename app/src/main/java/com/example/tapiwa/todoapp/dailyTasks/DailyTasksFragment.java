package com.example.tapiwa.todoapp.dailyTasks;

import android.app.Activity;
import android.graphics.Color;
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

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskAdapter;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.Utils.ProgressTracker;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Iterator;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DailyTasksFragment extends androidx.fragment.app.Fragment {

    public static TextView date;
    private static ListView goalsList;
    private static View tasksPageView;
    private static TextView percentageTxtV;
    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private static String CURRENT_DATE;
    private static FileHandler fileHandler;
    private Activity activity;
    private static ProgressTracker progressTracker;
    private static int clickedTask;


    public DailyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksPageView = inflater.inflate(R.layout.fragment_daily_tasks, container, false);
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        initializeViews();
        initializeVariables();
        calculatePercentage();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return tasksPageView;
    }

    @Override
    public void onResume() {
        retrieveSavedTasks();
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
          clickedTask = info.position;

        switch (item.getItemId()) {
            case R.id.rename_task:
                MainActivity.inputRequest.setInputRequest(InputRequests.InputRequestType.RENAME_PROJECT);
                MainActivity.getInputForFragment(MainActivity.visibleFragment);
                return true;
            case R.id.delete_task:
                deleteTask(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public static void renameTask(String newTitle) {
        Task task = tasksList.get(clickedTask);
        task.setTask(newTitle);
        tasksList.set(clickedTask, task);
        adapter.notifyDataSetChanged();
    }

    private void deleteTask(int clickedTask) {
        tasksList.remove(clickedTask);
        adapter.notifyDataSetChanged();
    }

    public static void addTask(String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");
        tasksList.add(newTask);
        goalsList.setAdapter(adapter);
        updateProgressTracker();
        adapter.notifyDataSetChanged();
    }

    public static void updateProgressTracker() {
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        progressTracker.updateCounter(list);
    }

    private void saveTasks() {
        String tasksJson = convertTasksListToJsonString();
        fileHandler.saveFile(getString(R.string.DAILY_TASKS_FILE), tasksJson);
    }

    private void retrieveSavedTasks() {
        JSONObject tasksJson = fileHandler.readFile(getString(R.string.DAILY_TASKS_FILE));
        populateTaskList(tasksJson);
    }

    private String convertTasksListToJsonString() {
        Gson gson = new Gson();
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        return gson.toJson(list);
    }

    private void initializeVariables() {
        tasksList = new LinkedList<>();
        fileHandler = new FileHandler(getContext());
        progressTracker = new ProgressTracker(getContext(), getString(R.string.DAILY_TASKS_FILE));
    }

    private void populateTaskList(JSONObject tasksJson) {
        if (tasksJson != null) {
            Gson gson = new Gson();
            TaskList list = gson.fromJson(tasksJson.toString(), TaskList.class);

            if (list != null && list.getTaskList().size() > 0) {
                tasksList = list.getTaskList();
                adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);
                goalsList.setAdapter(adapter);
            }
        }
    }

    private void initializeViews() {
        percentageTxtV = tasksPageView.findViewById(R.id.percentage_completed);
        date = tasksPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        goalsList = tasksPageView.findViewById(R.id.goals_lstV);
        registerForContextMenu(goalsList);
        adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);

        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task updatedTask = tasksList.get(i);

                if (!updatedTask.getStatus().equals("completed")) {
                    updatedTask.setStatus("completed");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText("Congratulations!").setContentText("Congratulations on finishing all your tasks");
                        dg.show();
                        tasksList.clear();
                    }

                } else {
                    updatedTask.setStatus("uncompleted");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                }
                updateProgressTracker();
                calculatePercentage();
            }
        });
    }



    private void calculatePercentage() {
        percentageTxtV.setTextColor(Color.rgb(208, 35, 35));
        if (tasksList.size() == 0) {
            percentageTxtV.setText(Integer.toString(0));
            return;
        } else {
            int completedTasks = countCompletedTasks();
            int percentage = (int) Math.floor(((double) completedTasks / tasksList.size()) * 100);
            percentageTxtV.setText(Integer.toString(percentage) + "%");
            checkTasksCompletion();
            return;
        }
    }

    private boolean checkTasksCompletion() {
        Iterator iter = tasksList.iterator();
        while (iter.hasNext()) {
            Task task = (Task) iter.next();
            if (task.getStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private int countCompletedTasks() {
        Iterator iter = tasksList.iterator();
        int i = 0;

        while (iter.hasNext()) {
            Task task = (Task) iter.next();
            if (task.getStatus().equals("completed")) {
                ++i;
            }
        }
        return i;
    }
}
