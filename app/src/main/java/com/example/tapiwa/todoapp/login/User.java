package com.example.tapiwa.todoapp.login;

import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;

import java.util.ArrayList;

public class User {

    String uid;
    String userName;
    String email;
    String dailyProjects;
    String weeklyProjects;
    String longTermProjects;
    String personalProjects;
    String yearlyProjects;
    ArrayList<String> sharedProjectKeys;

    public User() {
        //required empty constructor
    }

    public String getDailyProjects() {
        return dailyProjects;
    }

    public String getYearlyProjects() {
        return yearlyProjects;
    }

    public void setYearlyProjects(String yearlyProjects) {
        this.yearlyProjects = yearlyProjects;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public void setDailyProjects(String dailyProjects) {
        this.dailyProjects = dailyProjects;
    }

    public String getWeeklyProjects() {
        return weeklyProjects;
    }

    public void setWeeklyProjects(String weeklyProjects) {
        this.weeklyProjects = weeklyProjects;
    }

    public String getLongTermProjects() {
        return longTermProjects;
    }

    public void setLongTermProjects(String longTermProjects) {
        this.longTermProjects = longTermProjects;
    }

    public String getPersonalProjects() {
        return personalProjects;
    }

    public void setPersonalProjects(String personalProjects) {
        this.personalProjects = personalProjects;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getSharedProjectKeys() {
        return sharedProjectKeys;
    }

    public void setSharedProjectKeys(ArrayList<String> sharedProjectKeys) {
        this.sharedProjectKeys = sharedProjectKeys;
    }
}
