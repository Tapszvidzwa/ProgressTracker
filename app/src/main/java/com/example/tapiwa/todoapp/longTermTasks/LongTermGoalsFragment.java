package com.example.tapiwa.todoapp.longTermTasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskAdapter;
import com.example.tapiwa.todoapp.TaskList;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.appcompat.app.AlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class LongTermGoalsFragment extends androidx.fragment.app.Fragment {

    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private static ListView goalsList;
    private View tasksPageView;
    private FileHandler fileHandler;
    private int uncompletedTasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksPageView = inflater.inflate(R.layout.fragment_long_term_tasks, container, false);
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
        saveTasks();
        super.onPause();
    }


    public LongTermGoalsFragment() {
        // Required empty public constructor
    }

    public static void addNewTask(final String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");

        //add it to the tasks list
        tasksList.add(newTask);
        goalsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void saveTasks() {
        String tasksJson = convertTasksListToJsonString();
        fileHandler.saveFile(getString(R.string.LONG_TERM_PROJECTS_FILE), tasksJson);
    }

    private void retrieveSavedTasks() {
        JSONObject tasksJson = fileHandler.readFile(getString(R.string.LONG_TERM_PROJECTS_FILE));
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
        uncompletedTasks = 0;
        fileHandler = new FileHandler(getContext());
    }

    private void initializeViews() {
        goalsList = tasksPageView.findViewById(R.id.five_year_goals_lstV);
        adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);

        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task updatedTask = tasksList.get(i);

                if (!updatedTask.getStatus().equals("completed")) {
                    updatedTask.setStatus("completed");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                    --uncompletedTasks;

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText(getString(R.string.congratulations)).setContentText(getString(R.string.congratulatory_msg));
                        dg.show();


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
                    ++uncompletedTasks;

                }
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
                        uncompletedTasks = 0;
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

}
