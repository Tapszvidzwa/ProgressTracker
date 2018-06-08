package com.example.tapiwa.todoapp.dailyProjects;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.CompletionBar;
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

    private ListView goalsList;
    public static ImageView restingDude;
    public static TextView noGoalsText, date;
    private View tasksPageView;
    private CompletionBar completionBar;
    private TextView percentageTxtV;
    private LinkedList<Task> tasksList;
    private TaskAdapter adapter;
    private LinearLayout parentLayout;
    private FloatingActionButton addTask;
    private String CURRENT_DATE;


    private final String GOALS = "Goals";

    private int totalTasks;
    private int uncompletedTasks;
    private int initialBarlength;


    public DailyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        tasksPageView = inflater.inflate(R.layout.daily_tasks_fragment, container, false);
        addTask = tasksPageView.findViewById(R.id.add_task);
        CURRENT_DATE = DateFormat.getDateInstance().format(System.currentTimeMillis());
        initializeViews();
        initializeVariables();
        getGoals();
        calculatePercentage();
        return tasksPageView;
    }

    private void initializeVariables() {
        tasksList = new LinkedList<>();
        uncompletedTasks = 0;
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

            if(list != null) {
                tasksList = list.getTaskList();
                adapter = new TaskAdapter(getActivity().getApplicationContext(),R.layout.item_goal_list, tasksList);
                goalsList.setAdapter(adapter);
            }

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    private void initializeViews() {

        percentageTxtV = tasksPageView.findViewById(R.id.percentage_completed);
        restingDude = tasksPageView.findViewById(R.id.resting_dude);
        noGoalsText = tasksPageView.findViewById(R.id.no_goals_text);
        parentLayout = tasksPageView.findViewById(R.id.fragment_tasks_layout);
        date = tasksPageView.findViewById(R.id.current_date);
        date.setText(CURRENT_DATE);
        goalsList = tasksPageView.findViewById(R.id.goals_lstV);
        adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });

        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task updatedTask = tasksList.get(i);

                if(!updatedTask.getStatus().equals("completed")) {
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
        percentageTxtV.setTextColor(Color.rgb(208,35,35));

        if(tasksList.size() == 0) {
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
        while(iter.hasNext()) {
            Task task = (Task) iter.next();
            if(task.getStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private int countCompletedTasks() {
        Iterator iter = tasksList.iterator();
        int i = 0;

        while(iter.hasNext()) {
            Task task = (Task) iter.next();
            if(task.getStatus().equals("completed")) {
                ++i;
            }
        }

        return i;
    }

    public void addNewTask() {

        //Get title of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle("Add a new task");

        int maxLength = 200;
        final EditText givenTitle = new EditText(getActivity().getApplicationContext());
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(givenTitle.getText().toString().length() > 0) {

                    //create the new task
                    Task newTask = new Task();
                    newTask.setTask(givenTitle.getText().toString());
                    newTask.setStatus("uncompleted");

                    //add it to the tasks list
                    tasksList.add(newTask);
                    adapter.notifyDataSetChanged();
                    totalTasks = tasksList.size();
                    calculatePercentage();
                } else {
                    Toasty.info(getActivity().getApplicationContext(), "Please provide a task description", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResume(){
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
}
