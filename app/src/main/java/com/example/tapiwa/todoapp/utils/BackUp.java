package com.example.tapiwa.todoapp.utils;

import android.content.Context;

import com.example.tapiwa.todoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import static com.example.tapiwa.todoapp.utils.Constants.BACKUP_PATH;

public class BackUp {

    private String dailyTasksJson;
    private String weeklyTasksJson;
    private String longTermTasksJson;
    private String oneYearTasksJson;
    private String personalProjectsJson;
    private String userId;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FileHandler fileHandler;
    public Boolean syncCompletionStatus = false;

    public BackUp(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        this.fileHandler = new FileHandler(context);
    }

    public BackUp() {
    }

    public void runBackupFiles() {
        setDailyTasksJson(getLocalDailyTasksJson());
        setWeeklyTasksJson(getLocalWeeklyTasksJson());
        setLongTermTasksJson(getLocalLongTermTasksJson());
        setPersonalProjectsJson(getLocalPersonalProjectsJson());
        setOneYearTasksJson(getLocalOneYearTasksJson());
        db = FirebaseFirestore.getInstance();
        db.document(BACKUP_PATH + userId).set(this);
    }

    public void runSyncLocalFiles() {
        fileHandler.clearAllFiles();

        db.document(BACKUP_PATH + userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BackUp backupFiles = documentSnapshot.toObject(BackUp.class);
                try {
                    fileHandler.saveFile(context.getString(R.string.DAILY_TASKS_FILE), backupFiles.getDailyTasksJson());
                    fileHandler.saveFile(context.getString(R.string.WEEKLY_TASKS_FILE), backupFiles.getWeeklyTasksJson());
                    fileHandler.saveFile(context.getString(R.string.LONG_TERM_PROJECTS_FILE), backupFiles.getLongTermTasksJson());
                    fileHandler.saveFile(context.getString(R.string.YEARLY_TASKS_FILE), backupFiles.getOneYearTasksJson());
                    fileHandler.saveFile(context.getString(R.string.PERSONAL_PROJECTS_FILE), backupFiles.getPersonalProjectsJson());
                    syncCompletionStatus = true;
                } catch (NullPointerException e) {
                    syncCompletionStatus = false;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                syncCompletionStatus = false;
            }
        });
    }

    public String getDailyTasksJson() {
        return dailyTasksJson;
    }

    public String getOneYearTasksJson() {
        return oneYearTasksJson;
    }

    public Boolean isSyncCompleted() {
        return syncCompletionStatus;
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

    public void setOneYearTasksJson(String oneYearTasksJson) {
        this.oneYearTasksJson = oneYearTasksJson;
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

    public String getLocalOneYearTasksJson() {
        JSONObject tasks = fileHandler.readFile(context.getString(R.string.YEARLY_TASKS_FILE));
        return tasks.toString();
    }
}
