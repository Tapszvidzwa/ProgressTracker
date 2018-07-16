package com.example.tapiwa.todoapp.weeklyGoals;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Task;
import com.example.tapiwa.todoapp.TaskAdapter;
import com.example.tapiwa.todoapp.TaskList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import androidx.appcompat.app.AlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class WeeklyTasksFragment extends Fragment {

    private ListView goalsList;
    private View tasksPageView;
    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;

    public WeeklyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksPageView = inflater.inflate(R.layout.fragment_weekly_tasks, container, false);
        initializeViews();
        initializeVariables();
        getGoals();

        return tasksPageView;
    }

    private void initializeVariables() {
        tasksList = new LinkedList<>();
    }

    private void getGoals() {

        try {
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Weekly_tasks_file));
            tasksFile.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(tasksFile));

            Gson gson = new Gson();
            TaskList list = gson.fromJson(br, TaskList.class);

            if (list != null) {
                tasksList = list.getTaskList();
                adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);
                goalsList.setAdapter(adapter);
            }

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), getString(R.string.failed_file_loading), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    private void initializeViews() {
        goalsList = tasksPageView.findViewById(R.id.weekly_goals_lstV);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        TaskList list = new TaskList();

        list.setTaskList(tasksList);

        Gson gson = new Gson();
        String tasksJson = gson.toJson(list);

        FileOutputStream fos = null;
        try {
            //open tasks file
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Weekly_tasks_file));
            //create new file if the file does not exist
            tasksFile.createNewFile();
            //save/write the tasks to the tasks.json file
            fos = new FileOutputStream(tasksFile);
            byte[] tasksFileBytes = tasksJson.getBytes();
            fos.write(tasksFileBytes);
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

    public static void addNewTask(String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");

        tasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }
}
