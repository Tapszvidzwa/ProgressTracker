package com.example.tapiwa.todoapp.dailyProjects;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.util.Iterator;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class DailyTasksFragment extends Fragment {

    private static ListView goalsList;
    public static TextView date;
    private static View tasksPageView;
    private static TextView percentageTxtV;
    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private static String CURRENT_DATE;


    public DailyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        tasksPageView = inflater.inflate(R.layout.fragment_daily_tasks, container, false);
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        initializeViews();
        initializeVariables();
        getGoals();
        calculatePercentage();
        return tasksPageView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getGoals();
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
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.DailyGoalsFile));
            //create new file if the file does not exist
            tasksFile.createNewFile();
            //save/write the tasks to the tasks.json file
            fos = new FileOutputStream(tasksFile);
            byte[] tasksFileBytes = tasksJson.getBytes();
            fos.write(tasksFileBytes);
            fos.flush();

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT);
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
    }

    private void getGoals() {
        try {
            //open tasks file
            File tasksFile = new File(getActivity().getApplicationContext().getFilesDir(), getString(R.string.DailyGoalsFile));
            //create new file if the file does not exist
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
            Toasty.error(getActivity().getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    private void initializeViews() {

        percentageTxtV = tasksPageView.findViewById(R.id.percentage_completed);
        date = tasksPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        goalsList = tasksPageView.findViewById(R.id.goals_lstV);
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

    public static void addTask(String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");

        tasksList.add(newTask);
        adapter.notifyDataSetChanged();
    }
}
