package com.example.tapiwa.todoapp.sharedProjects;

import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SharedProjectTask;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SharedProjectModel implements Serializable {

    private String projectTitle;
    private ArrayList<SharedProjectTask> projectTasks;
    private ArrayList<String> memberEmails;
    private ArrayList<String> memberNames;
    private String lastModifiedtime;
    private String projectKey;
    private String percentageCompleted;

    public SharedProjectModel() {
    }

    public ArrayList<String> getMemberEmails() {
        return memberEmails;
    }

    public void setMemberEmails(ArrayList<String> memberEmails) {
        this.memberEmails = memberEmails;
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

    public ArrayList<SharedProjectTask> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(ArrayList<SharedProjectTask> projectTasks) {
        this.projectTasks = projectTasks;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public ArrayList<String> getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(ArrayList<String> memberNames) {
        this.memberNames = memberNames;
    }

    public String getPercentageCompleted() {
        if(percentageCompleted == null) {
            return "0%";
        } else {
            return percentageCompleted;
        }
    }

    public void setPercentageCompleted() {
        if(projectTasks.size() == 0) {
            this.percentageCompleted = "0%";
        } else {
            double completed = 0;
            for (int i = 0; i < projectTasks.size(); i++) {
                if (projectTasks.get(i).getCompletionStatus().equals("completed")) {
                    ++completed;
                }
            }
            DecimalFormat numberformat = new DecimalFormat("#");
            double result = ((double) completed / projectTasks.size()) * 100;
            this.percentageCompleted = numberformat.format(result) + "%";
        }
    }
}
