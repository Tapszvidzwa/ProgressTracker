package com.example.tapiwa.todoapp.longTermGoals;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
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


public class LongTermGoalsFragment extends Fragment {

    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private ListView goalsList;
    private View tasksPageView;


    private int uncompletedTasks;

    public LongTermGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tasksPageView = inflater.inflate(R.layout.fragment_long_term_goals, container, false);
        initializeViews();
        initializeVariables();
        getGoals();

        return tasksPageView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
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
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Five_year_tasks_file));
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

    private void initializeVariables() {
        tasksList = new LinkedList<>();
        uncompletedTasks = 0;
    }


    private void getGoals() {

        try {
            //open tasks file
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.Five_year_tasks_file));
            //create new file if the file does not exist
            tasksFile.createNewFile();

            if (tasksFile.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(tasksFile));

                Gson gson = new Gson();

                TaskList list = gson.fromJson(br, TaskList.class);

                if (list != null) {
                    tasksList = list.getTaskList();
                    adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);
                    goalsList.setAdapter(adapter);
                }
            } else {
                getGoals();
            }

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), getString(R.string.failed_file_loading), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
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

    public static void addNewTask(final String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");

        //add it to the tasks list
        tasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }

}
