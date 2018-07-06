package com.example.tapiwa.todoapp.longTermGoals;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
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
import java.util.Iterator;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class longTermGoals extends Fragment {

    private ListView goalsList;
    public static ImageView restingDude;
    public static TextView noGoalsText;
    private LinearLayout progressBar, progressBarBorder;
    private View tasksPageView;
    private CompletionBar completionBar;
    private TextView percentageTxtV;
    private LinkedList<Task> tasksList;
    private TaskAdapter adapter;
    private LinearLayout parentLayout;
    private FloatingActionButton addTask;
    private TextView taskTypeLabelTxtV;


    private final String GOALS = "Goals";

    private int totalTasks;
    private int uncompletedTasks;
    private int initialBarlength;

    public longTermGoals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        tasksPageView = inflater.inflate(R.layout.five_year_tasks_fragment, container, false);
        addTask = tasksPageView.findViewById(R.id.five_year_weekly_add_task);
        taskTypeLabelTxtV= tasksPageView.findViewById(R.id.five_year_tasks_type_label);


        taskTypeLabelTxtV.setText(getString(R.string.five_year_fragment));

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        initialBarlength = display.getWidth();

        initializeViews();
        initializeVariables();
        getGoals();

        return tasksPageView;
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

            if(tasksFile.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(tasksFile));

                Gson gson = new Gson();

                TaskList list = gson.fromJson(br, TaskList.class);

                if (list != null) {
                    tasksList = list.getTaskList();
                    adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);
                    goalsList.setAdapter(adapter);
                    totalTasks = tasksList.size();
                    uncompletedTasks = countUncompletedTasks();
                    updateCompletionBar();
                }
            } else {
                getGoals();
            }

        } catch (IOException e) {
            Toasty.error(getActivity().getApplicationContext(),getString(R.string.failed_file_loading), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    private void initializeViews() {
        percentageTxtV = tasksPageView.findViewById(R.id.five_year_percentage_completed);
        restingDude = tasksPageView.findViewById(R.id.five_year_resting_dude);
        noGoalsText = tasksPageView.findViewById(R.id.five_year_no_goals_text);
        parentLayout = tasksPageView.findViewById(R.id.five_year_fragment_tasks_layout);

        progressBar = tasksPageView.findViewById(R.id.five_year_progress_inner_bar);
        progressBarBorder = tasksPageView.findViewById(R.id.five_year_progress_outer_bar);
        goalsList = tasksPageView.findViewById(R.id.five_year_goals_lstV);
        adapter = new TaskAdapter(getActivity().getApplicationContext(), R.layout.item_goal_list, tasksList);
        completionBar = new CompletionBar();
        updateCompletionBar();

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
                    updateCompletionBar();
                }
        });
    }


    public void permissionClearTasks(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to clear your completed tasks?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                totalTasks = 0;
                                uncompletedTasks = 0;
                               tasksList.clear();
                               updateCompletionBar();
                               adapter.notifyDataSetChanged();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
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
        while(iter.hasNext()) {
            Task task = (Task) iter.next();
            if(task.getStatus().equals("uncompleted")) {
                return false;
            }
        }
        return true;
    }

    private void updateCompletionBar() {

        int total = totalTasks;
        int uncompleted = uncompletedTasks;

        completionBar.updateCompletionBar(
                total - uncompleted,
                uncompleted,
                initialBarlength,
                progressBar,
                percentageTxtV
        );
    }


    private int countUncompletedTasks() {

        Iterator iter = tasksList.iterator();
        int i = 0;

        while(iter.hasNext()) {
            Task task = (Task) iter.next();
            if(task.getStatus().equals("uncompleted")) {
                ++i;
            }
        }

        return i;
    }



    public void addNewTask() {

        //Get title of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    //    builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle(getString(R.string.add_new_task));

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
                    uncompletedTasks = countUncompletedTasks();
                    updateCompletionBar();
                } else {
                    Toasty.info(getActivity().getApplicationContext(), getString(R.string.no_task_entered), Toast.LENGTH_SHORT).show();
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
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        initialBarlength = display.getWidth();
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
}
