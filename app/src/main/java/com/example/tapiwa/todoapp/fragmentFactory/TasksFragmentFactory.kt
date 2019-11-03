package com.example.tapiwa.todoapp.fragmentFactory

import android.app.Application
import android.os.Bundle

import com.example.tapiwa.todoapp.navigation.BottomNavigationDrawerFragment
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectFragment
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SingleProjectFragment
import com.example.tapiwa.todoapp.tasksDaily.DailyTasksFragment
import com.example.tapiwa.todoapp.tasksLongTerm.LongTermGoalsFragment
import com.example.tapiwa.todoapp.tasksWeekly.WeeklyTasksFragment
import com.example.tapiwa.todoapp.tasksYearly.YearlyGoalsFragment

import androidx.fragment.app.Fragment

class TasksFragmentFactory : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    fun getFragment(fragmentName: FragmentName?): Fragment? {
        if (fragmentName == null) {
            return null
        }

        when (fragmentName) {
            FragmentName.CUSTOM_PROJECTS -> return PersonalProjectsContainerFragment()
            FragmentName.GROUP_PROJECTS -> return SharedProjectsFragment()
            FragmentName.LONG_TERM_TASKS -> return createFragment(LongTermGoalsFragment(), "long_term_tasks.json")
            FragmentName.YEARLY_TASKS -> return createFragment(YearlyGoalsFragment(), "yearly_tasks.json")
            FragmentName.WEEKLY_TASKS -> return createFragment(WeeklyTasksFragment(), "weekly_tasks.json")
            FragmentName.DAILY_TASKS -> return createFragment(DailyTasksFragment(), "daily_tasks.json")
            FragmentName.SINGLE_GROUP_PROJECT -> return createFragment(SingleProjectFragment(), "none")
            FragmentName.CUSTOM_PROJECT -> return createFragment(PersonalProjectFragment(), "none")
            FragmentName.BOTTOM_SHEET_MENU -> return BottomNavigationDrawerFragment()
            else -> return DailyTasksFragment()
        }
    }

    private fun createFragment(fragment: Fragment, filename: String): Fragment {
        val bundle = Bundle()
        bundle.putString("filename", filename)
        fragment.arguments = bundle
        return fragment
    }
}
