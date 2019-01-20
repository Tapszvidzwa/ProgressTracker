package com.example.tapiwa.todoapp.templates;

import android.os.Bundle;
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

import cn.pedant.SweetAlert.SweetAlertDialog;


public abstract class BaseFragment extends androidx.fragment.app.Fragment {

    private static ListView goalsList;
    private static View tasksPageView;
    private static LinkedList<Task> tasksList;
    private static TaskAdapter adapter;
    private static FileHandler fileHandler;
    private static ProgressTracker progressTracker;
    private static int clickedTask;
    private String filename;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        setFileName(bundle.getString("filename"));
        tasksPageView = inflater.inflate(R.layout.template_tasks_list, container, false);
        initializeViews();
        initializeDependencies();
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
                MainActivity.getInputForFragment(MainActivity.visibleFragment, tasksList.get(clickedTask).getTask());
                return true;
            case R.id.delete_task:
                deleteTask(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public static void renameTask(String newTitle) {
        Task task = tasksList.get(clickedTask);
        task.setTask(newTitle);
        tasksList.set(clickedTask, task);
        adapter.notifyDataSetChanged();
    }

    private final void deleteTask(int clickedTask) {
        tasksList.remove(clickedTask);
        adapter.notifyDataSetChanged();
    }

    public final static void addTask(String task) {
        Task newTask = new Task();
        newTask.setTask(task);
        newTask.setStatus("uncompleted");
        tasksList.add(newTask);
        updateProgressTracker();
        adapter.notifyDataSetChanged();
    }

    public final static void updateProgressTracker() {
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        progressTracker.updateCounter(list);
    }

    public final void saveTasks() {
        String tasksJson = convertTasksListToJsonString();
        fileHandler.saveFile(filename, tasksJson);
    }

    private final void retrieveSavedTasks() {
        JSONObject tasksJson = fileHandler.readFile(filename);
        populateTaskList(tasksJson);
    }

    private final String convertTasksListToJsonString() {
        Gson gson = new Gson();
        TaskList list = new TaskList();
        list.setTaskList(tasksList);
        return gson.toJson(list);
    }

    private final void initializeDependencies() {
        tasksList = new LinkedList<>();
        fileHandler = new FileHandler(getContext());
        progressTracker = new ProgressTracker(getContext(), filename);
    }

    private final void populateTaskList(JSONObject tasksJson) {
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

    private final void initializeViews() {
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
            }
        });
    }

    private final boolean checkTasksCompletion() {
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
