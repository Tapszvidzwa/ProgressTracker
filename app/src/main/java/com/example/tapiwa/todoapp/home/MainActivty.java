package com.example.tapiwa.todoapp.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.dailyProjects.DailyTasksFragment;
import com.example.tapiwa.todoapp.longTermGoals.LongTermGoalsFragment;
import com.example.tapiwa.todoapp.oneYearGoals.YearlyGoalsFragment;
import com.example.tapiwa.todoapp.personalProjects.PersonalProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.weeklyGoals.WeeklyTasksFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.DAILY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.LONG_TERM_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.MONTHLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.PERSONAL_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.SHARED_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.WEEKLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivty.FragmentName.YEARLY_TASKS;
import static com.example.tapiwa.todoapp.personalProjects.PersonalProjectsFragment.InputRequestType.CREATE_NEW_PROJECT;
import static com.example.tapiwa.todoapp.personalProjects.PersonalProjectsFragment.InputRequestType.RENAME_PROJECT;
import static com.example.tapiwa.todoapp.personalProjects.PersonalProjectsFragment.inputRequestType;

public class MainActivty extends AppCompatActivity {

    //TODO when app loads, create all json files at once
    private DrawerLayout mDrawerLayout;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton getInputFab;
    public static FragmentName visibleFragment;
    public static Activity activity;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        bottomAppBar = findViewById(R.id.bar);
        getInputFab = findViewById(R.id.fab);
        visibleFragment = DAILY_TASKS;
        activity = this;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        getInputFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (visibleFragment) {
                    case DAILY_TASKS:
                        getInputForFragment(DAILY_TASKS);
                        break;
                    case WEEKLY_TASKS:
                        getInputForFragment(WEEKLY_TASKS);
                        break;
                    case YEARLY_TASKS:
                        getInputForFragment(YEARLY_TASKS);
                        break;
                    case MONTHLY_TASKS:
                        getInputForFragment(MONTHLY_TASKS);
                        break;
                    case LONG_TERM_TASKS:
                        getInputForFragment(LONG_TERM_TASKS);
                        break;
                    case SHARED_PROJECTS:
                        getInputForFragment(SHARED_PROJECTS);
                        break;
                    case PERSONAL_PROJECTS:
                        inputRequestType = CREATE_NEW_PROJECT;
                        getInputForFragment(PERSONAL_PROJECTS);
                        break;
                    default:
                        getInputForFragment(DAILY_TASKS);
                }
            }
        });

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerLayout.closeDrawers();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        setupDrawer(navigationView);
        switchToFragment(DAILY_TASKS);
        switchToolBarName();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.daily_tasks:
                switchToFragment(FragmentName.DAILY_TASKS);
                break;
            case R.id.weekly_tasks:
                switchToFragment(FragmentName.WEEKLY_TASKS);
                break;
            case R.id.yearly_goals:
                switchToFragment(FragmentName.YEARLY_TASKS);
                break;
            case R.id.five_year_goals:
                switchToFragment(FragmentName.LONG_TERM_TASKS);
                break;
            case R.id.personal_projects:
                switchToFragment(FragmentName.PERSONAL_PROJECTS);
                break;
            case R.id.shared_projects:
                switchToFragment(FragmentName.SHARED_PROJECTS);
                break;
            default:
                switchToFragment(FragmentName.DAILY_TASKS);
        }

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    public static void getInputForFragment(final FragmentName requestingFragment) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add a new task");

        int maxLength = 200;
        final EditText givenTitle = new EditText(activity.getApplicationContext());
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (givenTitle.getText().toString().length() > 0) {
                    sendInputToVisibleFragment(requestingFragment, givenTitle.getText().toString());
                } else {
                    Toasty.info(activity.getApplicationContext(),
                            "Please provide a task description",
                            Toast.LENGTH_SHORT).show();
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

    private static void sendInputToVisibleFragment(final FragmentName requestingFragment, final String input) {
        switch (requestingFragment) {
            case DAILY_TASKS:
                DailyTasksFragment.addTask(input);
                break;
            case WEEKLY_TASKS:
                WeeklyTasksFragment.addNewTask(input);
                break;
            case YEARLY_TASKS:
                YearlyGoalsFragment.addNewTask(input);
                break;
            case LONG_TERM_TASKS:
                LongTermGoalsFragment.addNewTask(input);
                break;
            case SHARED_PROJECTS:
                SharedProjectsFragment.addProject(input);
                break;
            case PERSONAL_PROJECTS:
                if(inputRequestType == CREATE_NEW_PROJECT) {
                    PersonalProjectsFragment.addProject(input);
                } if(inputRequestType == RENAME_PROJECT) {
                    PersonalProjectsFragment.renameProject(input);
                }
                break;
            default:
                break;
        }
    }

    private void switchToFragment(FragmentName fragmentName) {
        visibleFragment = fragmentName;
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_holder, getFragment(fragmentName))
                .commit();
        switchToolBarName();
    }

    private void switchToolBarName() {

        switch(visibleFragment) {
            case PERSONAL_PROJECTS:
                toolbar.setTitle(getString(R.string.personal_projects_fragment_title));
                break;
            case SHARED_PROJECTS:
                 toolbar.setTitle(getString(R.string.shared_projects_fragment_title));
                 break;
            case LONG_TERM_TASKS:
                toolbar.setTitle(getString(R.string.long_term_goals_title));
                break;
            case YEARLY_TASKS:
                toolbar.setTitle(getString(R.string.yearly_tasks_fragment_title));
                break;
            case WEEKLY_TASKS:
                toolbar.setTitle(getString(R.string.weekly_tasks_fragment_title));
                break;
            case DAILY_TASKS:
                toolbar.setTitle(getString(R.string.daily_tasks_fragment_title));
                break;
            default:
                toolbar.setTitle(getString(R.string.daily_tasks_fragment_title));
                break;
        }
    }

    private android.app.Fragment getFragment(FragmentName fragmentName) {

        switch (fragmentName) {
            case PERSONAL_PROJECTS:
                return new PersonalProjectsFragment();
            case SHARED_PROJECTS:
                return new SharedProjectsFragment();
            case LONG_TERM_TASKS:
                return new LongTermGoalsFragment();
            case YEARLY_TASKS:
                return new YearlyGoalsFragment();
            case WEEKLY_TASKS:
                return new WeeklyTasksFragment();
            case DAILY_TASKS:
                return new DailyTasksFragment();
            default:
                return new DailyTasksFragment();
        }
    }

    enum FragmentName {
        DAILY_TASKS, WEEKLY_TASKS, MONTHLY_TASKS, YEARLY_TASKS, LONG_TERM_TASKS,
        PERSONAL_PROJECTS, SHARED_PROJECTS
    }
}
