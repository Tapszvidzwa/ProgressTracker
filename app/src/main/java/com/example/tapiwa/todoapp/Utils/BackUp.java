package com.example.tapiwa.todoapp.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;

import static com.example.tapiwa.todoapp.Utils.Constants.BACKUP_PATH;
import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;
import static com.example.tapiwa.todoapp.home.MainActivity.fileHandler;

public class BackUp {

    private String dailyTasksJson;
    private String weeklyTasksJson;
    private String longTermTasksJson;
    private String personalProjectsJson;
    private String userId;
    private Context context;
    private String TAG = "BACKUP";

    public BackUp(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    public void runBackupFiles() {
        setDailyTasksJson(getLocalDailyTasksJson());
        setWeeklyTasksJson(getLocalWeeklyTasksJson());
        setLongTermTasksJson(getLocalLongTermTasksJson());
        setPersonalProjectsJson(getLocalPersonalProjectsJson());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document(BACKUP_PATH + userId).set(this);
    }

    public String getDailyTasksJson() {
        return dailyTasksJson;
    }

    public String getWeeklyTasksJson() {
        return weeklyTasksJson;
    }

    public String getLongTermTasksJson() {
        return longTermTasksJson;
    }

    public String getPersonalProjectsJson() {
        return personalProjectsJson;
    }

    public void setDailyTasksJson(String dailyTasksJson) {
        this.dailyTasksJson = dailyTasksJson;
    }

    public void setWeeklyTasksJson(String weeklyTasksJson) {
        this.weeklyTasksJson = weeklyTasksJson;
    }

    public void setLongTermTasksJson(String longTermTasksJson) {
        this.longTermTasksJson = longTermTasksJson;
    }

    public void setPersonalProjectsJson(String personalProjectsJson) {
        this.personalProjectsJson = personalProjectsJson;
    }

    public String getLocalPersonalProjectsJson() {
        JSONObject tasksJson = fileHandler.readFile(context.getString(R.string.PERSONAL_PROJECTS_FILE));
        return tasksJson.toString();
    }

    public String getLocalLongTermTasksJson() {
        JSONObject tasksJson = fileHandler.readFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE));
        return tasksJson.toString();
    }

    public String getLocalWeeklyTasksJson() {
        JSONObject tasksJson = fileHandler.readFile(context.getString(R.string.WEEKLY_TASKS_FILE));
        return tasksJson.toString();
    }

    public String getLocalDailyTasksJson() {
        JSONObject tasks = fileHandler.readFile(context.getString(R.string.DAILY_TASKS_FILE));
        return tasks.toString();
    }
}
