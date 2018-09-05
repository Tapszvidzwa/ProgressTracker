package com.example.tapiwa.todoapp.personalProjects.personalProject;

import com.example.tapiwa.todoapp.Utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.example.tapiwa.todoapp.Utils.Constants.PERSONAL_PROJECT_KEY_SIZE;

public class PersonalProjectModel {

    private String projectTitle;
    private ArrayList<PersonalProjectTask> projectTasks;
    private String lastModifiedtime;
    private String projectKey;
    private String percentageCompleted;

    public PersonalProjectModel() {
    }

    public String getLastModifiedtime() {
        return lastModifiedtime;
    }

    public void setLastModifiedtime(String lastModifiedtime) {
        this.lastModifiedtime = lastModifiedtime;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setProjectTasks(ArrayList<PersonalProjectTask> projectTasks) {
        this.projectTasks = projectTasks;
    }

    public ArrayList<PersonalProjectTask> getProjectTasks() {
        return projectTasks;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey() {
        this.projectKey = generateRandomKey();
    }

    public String generateRandomKey() {
        StringBuilder randomWord = new StringBuilder();
        for (int i = 0; i < PERSONAL_PROJECT_KEY_SIZE; i++) {
            if (i < (PERSONAL_PROJECT_KEY_SIZE / 2)) {
                randomWord.append((char) Util.getRandomAlphabeticChar());
            } else {
                randomWord.append((char) Util.getRandomDigit());
            }
        }
        return randomWord.toString();
    }

    public String getPercentageCompleted() {
        if (percentageCompleted == null) {
            return "0";
        } else {
            return percentageCompleted;
        }
    }

    public void setPercentageCompleted() {
        if (projectTasks.size() == 0) {
            this.percentageCompleted = "0";
        } else {
            int num_completed = 0;
            for (int i = 0; i < projectTasks.size(); i++) {
                if (projectTasks.get(i).getCompletionStatus().equals("completed")) {
                    ++num_completed;
                }
            }

            DecimalFormat numberformat = new DecimalFormat("#");
            double result = ((double) num_completed / projectTasks.size()) * 100;
            this.percentageCompleted = numberformat.format(result);
        }
    }

}
