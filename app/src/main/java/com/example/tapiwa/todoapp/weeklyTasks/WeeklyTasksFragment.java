package com.example.tapiwa.todoapp.weeklyTasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskAdapter;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.Utils.ProgressTracker;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.appcompat.app.AlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class WeeklyTasksFragment extends androidx.fragment.app.Fragment {

    private static ListView goalsList;
    private View tasksPageView;
    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private FileHandler fileHandler;
    private static ProgressTracker progressTracker;
    private static int clickedTask;

    public WeeklyTasksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksPageView = inflater.inflate(R.layout.fragment_weekly_tasks, container, false);
        initializeViews();
        initializeVariables();
        return tasksPageView;
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

    public static void addTask(String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");
        tasksList.add(newTask);
        goalsList.setAdapter(adapter);
        updateProgressTracker();
        adapter.notifyDataSetChanged();
    }

    private void deleteTask(int clickedTask) {
        tasksList.remove(clickedTask);
        adapter.notifyDataSetChanged();
    }

    private void saveTasks() {
        String tasksJson = convertTasksListToJsonString();
        fileHandler.saveFile(getString(R.string.WEEKLY_TASKS_FILE), tasksJson);
    }

    private void retrieveSavedTasks() {
        JSONObject tasksJson = fileHandler.readFile(getString(R.string.WEEKLY_TASKS_FILE));
        populateTaskList(tasksJson);
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

    private String convertTasksListToJsonString() {
        Gson gson = new Gson();
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        return gson.toJson(list);
    }

    private void initializeVariables() {
        tasksList = new LinkedList<>();
        fileHandler = new FileHandler(getContext());
        progressTracker = new ProgressTracker(getContext(), getString(R.string.WEEKLY_TASKS_FILE));
    }

    private void initializeViews() {
        goalsList = tasksPageView.findViewById(R.id.weekly_goals_lstV);
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
                        dg.setTitleText(getString(R.string.congratulations))
                                .setContentText(getString(R.string.congratulatory_msg))
                                .show();

                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                permissionClearTasks();
                            }

                        }.start();
                    }
                } else {
                    updatedTask.setStatus("uncompleted");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                }
                updateProgressTracker();
            }
        });
    }

    public void permissionClearTasks() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to clear your completed tasks?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        tasksList.clear();
                        adapter.notifyDataSetChanged();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    public static void updateProgressTracker() {
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        progressTracker.updateCounter(list);
    }
}
