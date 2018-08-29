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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.dailyProjects.DailyTasksFragment;
import com.example.tapiwa.todoapp.longTermGoals.LongTermGoalsFragment;
import com.example.tapiwa.todoapp.oneYearGoals.YearlyGoalsFragment;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectFragment;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SingleProjectFragment;
import com.example.tapiwa.todoapp.weeklyGoals.WeeklyTasksFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType;
import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType.CREATE_NEW_PROJECT;
import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType.CREATE_NEW_TASK;
import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType.NONE;
import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType.RENAME_PROJECT;
import static com.example.tapiwa.todoapp.Utils.Constants.InputRequestType.RENAME_TASK;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.DAILY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.LONG_TERM_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.MONTHLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.PERSONAL_PROJECT;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.PERSONAL_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.SHARED_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.SINGLE_SHARED_PROJECT;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.WEEKLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.YEARLY_TASKS;
import static com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment.inputRequestType;

public class MainActivity extends AppCompatActivity {

    public static FragmentName visibleFragment;
    public static Activity activity;
    public static Toolbar toolbar;
    public static androidx.appcompat.app.ActionBar actionBar;
    public static FragmentManager fragmentManager;
    public static FileHandler fileHandler;
    private DrawerLayout mDrawerLayout;
    public static BottomAppBar bottomAppBar;
    private FloatingActionButton getInputFab;
    public static FirebaseAuth auth;
    public static BottomSheetDialogFragment bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        bottomAppBar = findViewById(R.id.bar);
        getInputFab = findViewById(R.id.fab);
        visibleFragment = DAILY_TASKS;
        fragmentManager = getSupportFragmentManager();
        activity = this;
        fileHandler = new FileHandler(activity.getApplicationContext());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        bottomSheetDialogFragment = new BottomNavigationDrawerFragment();
        final NavigationView navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        updateUpNavigationIcon();
        updateBottomBarMenu();
        initializeFab();

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.user_email);
        navUsername.setText(auth.getCurrentUser().getEmail());

        switchToFragment(DAILY_TASKS, null);
        updateToolBarName();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openAppropriateFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void openAppropriateFragment() {
        if(visibleFragment == SINGLE_SHARED_PROJECT) {
            switchToFragment(SHARED_PROJECTS, null);
        } else if(visibleFragment == PERSONAL_PROJECT) {
            switchToFragment(PERSONAL_PROJECTS, null);
        }
    }

    public static void updateBottomBarMenu() {
        switch (visibleFragment) {
            case SINGLE_SHARED_PROJECT:
                bottomAppBar.replaceMenu(R.menu.single_shared_project_toolbar_menu);
                break;
            default:
                bottomAppBar.replaceMenu(R.menu.default_menu);
        }
    }

    public static void updateUpNavigationIcon() {

        switch (visibleFragment) {
            case SINGLE_SHARED_PROJECT:
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                break;
            case PERSONAL_PROJECT:
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                break;
            default:
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public static void getInputForFragment(final FragmentName requestingFragment, InputRequestType requestType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getAppropriatePrompt(requestType));

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

    public static String getAppropriatePrompt(InputRequestType requestType) {
        if (requestType == null) {
            requestType = InputRequestType.NONE;
        }
        switch (requestType) {
            case ADD_GROUP_MEMBER:
                return "Enter new member's email address";
            case RENAME_PROJECT:
                return "Rename Project";
            case CREATE_NEW_PROJECT:
                return "Enter Project Title";
            case RENAME_TASK:
                return "Rename Task";
            default:
                return "Add a new task";
        }
    }

    private static void sendInputToVisibleFragment(final FragmentName requestingFragment, final String input) {
        //TODO find a cleaner way to do this to handle these if statements
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
                if (SharedProjectsFragment.inputRequestType == RENAME_PROJECT) {
                    SharedProjectsFragment.renameProject(input);
                    SharedProjectsFragment.inputRequestType = NONE;
                } else {
                    SharedProjectsFragment.addProject(input);
                }
                break;
            case PERSONAL_PROJECTS:
                if (inputRequestType == CREATE_NEW_PROJECT) {
                    PersonalProjectsContainerFragment.addProject(input);
                }
                if (inputRequestType == RENAME_PROJECT) {
                    PersonalProjectsContainerFragment.renameProject(input);
                }
                break;
            case SINGLE_SHARED_PROJECT:
                if (SingleProjectFragment.inputRequestType == InputRequestType.ADD_GROUP_MEMBER) {
                    SingleProjectFragment.addMember(input);
                    SingleProjectFragment.inputRequestType = InputRequestType.NONE;
                } else if (SingleProjectFragment.inputRequestType == InputRequestType.RENAME_TASK) {
                    SingleProjectFragment.renameTask(input);
                    SingleProjectFragment.inputRequestType = InputRequestType.NONE;
                } else {
                    SingleProjectFragment.addTask(input);
                }
                break;
            case PERSONAL_PROJECT:
                if(PersonalProjectFragment.inputRequestType == RENAME_TASK) {
                    PersonalProjectFragment.renameTask(input);
                } else {
                    //its definitely add new task
                    PersonalProjectFragment.addNewTask(input);
                }
                break;
            default:
                break;
        }
    }

    public static void switchToFragment(FragmentName fragmentName, Bundle bundle) {
        visibleFragment = fragmentName;
        androidx.fragment.app.Fragment fragment = getFragment(fragmentName);
        fragment.setArguments(bundle);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_holder, fragment)
                .commit();

        updateToolBarName();
        updateUpNavigationIcon();
        updateBottomBarMenu();
    }

    public static void updateToolBarName() {

        switch (visibleFragment) {
            case PERSONAL_PROJECTS:
                toolbar.setTitle(activity.getString(R.string.personal_projects_fragment_title));
                break;
            case SHARED_PROJECTS:
                toolbar.setTitle(activity.getString(R.string.shared_projects_fragment_title));
                break;
            case LONG_TERM_TASKS:
                toolbar.setTitle(activity.getString(R.string.long_term_goals_title));
                break;
            case YEARLY_TASKS:
                toolbar.setTitle(activity.getString(R.string.yearly_tasks_fragment_title));
                break;
            case WEEKLY_TASKS:
                toolbar.setTitle(activity.getString(R.string.weekly_tasks_fragment_title));
                break;
            case DAILY_TASKS:
                toolbar.setTitle(activity.getString(R.string.daily_tasks_fragment_title));
                break;
            default:
                toolbar.setTitle(activity.getString(R.string.daily_tasks_fragment_title));
                break;
        }
    }

    private static Fragment getFragment(FragmentName fragmentName) {

        switch (fragmentName) {
            case PERSONAL_PROJECTS:
                return new PersonalProjectsContainerFragment();
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
            case SINGLE_SHARED_PROJECT:
                return new SingleProjectFragment();
            case PERSONAL_PROJECT:
                return new PersonalProjectFragment();
            case BOTTOM_SHEET_MENU:
                return new BottomNavigationDrawerFragment();
            default:
                return new DailyTasksFragment();
        }
    }

    private void initializeFab() {
        getInputFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (visibleFragment) {
                    case DAILY_TASKS:
                        getInputForFragment(DAILY_TASKS, null);
                        break;
                    case WEEKLY_TASKS:
                        getInputForFragment(WEEKLY_TASKS, null);
                        break;
                    case YEARLY_TASKS:
                        getInputForFragment(YEARLY_TASKS, null);
                        break;
                    case MONTHLY_TASKS:
                        getInputForFragment(MONTHLY_TASKS, null);
                        break;
                    case LONG_TERM_TASKS:
                        getInputForFragment(LONG_TERM_TASKS, null);
                        break;
                    case SHARED_PROJECTS:
                        getInputForFragment(SHARED_PROJECTS, CREATE_NEW_PROJECT);
                        break;
                    case PERSONAL_PROJECTS:
                        inputRequestType = CREATE_NEW_PROJECT;
                        getInputForFragment(PERSONAL_PROJECTS, CREATE_NEW_PROJECT);
                        break;
                    case SINGLE_SHARED_PROJECT:
                        getInputForFragment(SINGLE_SHARED_PROJECT, null);
                        break;
                    case PERSONAL_PROJECT:
                        getInputForFragment(PERSONAL_PROJECT, CREATE_NEW_TASK);
                        break;
                    default:
                        getInputForFragment(DAILY_TASKS, null);
                }
            }
        });
    }

    public static void closeBottomSheet() {
        bottomSheetDialogFragment.dismiss();
    }

    public enum FragmentName {
        DAILY_TASKS, WEEKLY_TASKS, MONTHLY_TASKS, YEARLY_TASKS, LONG_TERM_TASKS,
        PERSONAL_PROJECTS, SHARED_PROJECTS, SINGLE_SHARED_PROJECT, PERSONAL_PROJECT, BOTTOM_SHEET_MENU
    }
}
