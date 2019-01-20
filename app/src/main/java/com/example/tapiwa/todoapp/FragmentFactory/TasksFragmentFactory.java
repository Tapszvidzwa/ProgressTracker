package com.example.tapiwa.todoapp.FragmentFactory;

import android.app.Application;
import android.os.Bundle;

import com.example.tapiwa.todoapp.home.BottomNavigationDrawerFragment;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectFragment;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SingleProjectFragment;
import com.example.tapiwa.todoapp.tasksDaily.DailyTasksFragment;
import com.example.tapiwa.todoapp.tasksLongTerm.LongTermGoalsFragment;
import com.example.tapiwa.todoapp.tasksWeekly.WeeklyTasksFragment;
import com.example.tapiwa.todoapp.tasksYearly.YearlyGoalsFragment;

import androidx.fragment.app.Fragment;

public class TasksFragmentFactory extends Application {

    public TasksFragmentFactory() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Fragment getFragment(FragmentName fragmentName) {
        if (fragmentName == null) {
            return null;
        }

        switch (fragmentName) {
            case PERSONAL_PROJECTS:
                return new PersonalProjectsContainerFragment();
            case SHARED_PROJECTS:
                return new SharedProjectsFragment();
            case LONG_TERM_TASKS:
                return createFragment(new LongTermGoalsFragment(), "long_term_tasks.json");
            case YEARLY_TASKS:
                return createFragment(new YearlyGoalsFragment(), "yearly_tasks.json");
            case WEEKLY_TASKS:
                return createFragment(new WeeklyTasksFragment(), "weekly_tasks.json");
            case DAILY_TASKS:
                return createFragment(new DailyTasksFragment(), "daily_tasks.json");
            case SINGLE_SHARED_PROJECT:
                return createFragment(new SingleProjectFragment(), "none");
            case PERSONAL_PROJECT:
                return createFragment(new PersonalProjectFragment(), "none");
            case BOTTOM_SHEET_MENU:
                return new BottomNavigationDrawerFragment();
            default:
                return new DailyTasksFragment();
        }
    }

    private Fragment createFragment(Fragment fragment, String filename) {
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        fragment.setArguments(bundle);
        return fragment;
    }
}
