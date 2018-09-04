package com.example.tapiwa.todoapp.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.BackUp;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.dailyTasks.DailyTasksFragment;
import com.example.tapiwa.todoapp.login.signIn.SignInActivity;
import com.example.tapiwa.todoapp.longTermTasks.LongTermGoalsFragment;
import com.example.tapiwa.todoapp.oneYearTasks.YearlyGoalsFragment;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectFragment;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SingleProjectFragment;
import com.example.tapiwa.todoapp.weeklyTasks.WeeklyTasksFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.ADD_GROUP_MEMBER;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.CREATE_NEW_PROJECT;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.CREATE_NEW_TASK;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.RENAME_PROJECT;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.RENAME_TASK;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.DAILY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.LONG_TERM_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.PERSONAL_PROJECT;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.PERSONAL_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.SHARED_PROJECTS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.SINGLE_SHARED_PROJECT;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.WEEKLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.FragmentName.YEARLY_TASKS;

public class MainActivity extends AppCompatActivity {

    public static FragmentName visibleFragment;
    public static Activity activity;
    public static Toolbar toolbar;
    public static androidx.appcompat.app.ActionBar actionBar;
    public static FragmentManager fragmentManager;
    public static FileHandler fileHandler;
    public static BottomAppBar bottomAppBar;
    private FloatingActionButton getInputFab;
    public static FirebaseAuth auth;
    public static BottomSheetDialogFragment bottomSheetDialogFragment;
    private ProgressBar progressBar;
    private Util Utils;
    public static InputRequests inputRequest = new InputRequests();
    private BackUp backUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activty);

        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        bottomAppBar = findViewById(R.id.bar);
        progressBar = findViewById(R.id.loading_progress_bar);
        getInputFab = findViewById(R.id.fab);
        visibleFragment = DAILY_TASKS;
        fragmentManager = getSupportFragmentManager();
        activity = this;
        fileHandler = new FileHandler(activity.getApplicationContext());
        bottomSheetDialogFragment = new BottomNavigationDrawerFragment();
        Utils = new Util(this);
        backUp = new BackUp(getApplicationContext(), auth.getUid());
        final NavigationView navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        setupBottomAppBar();
        updateUpNavigationIcon();
        updateBottomBarMenu();
        initializeFab();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.user_email);
        navUsername.setText(auth.getCurrentUser().getEmail());

        switchToFragment(DAILY_TASKS, null);
        updateToolBarName();
    }

    @Override
    protected void onStop() {
        runBackUp();
        super.onStop();
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

    private void runBackUp() {
        Utils.incrementLoginSessionCount();
        if (Utils.isReadyForBackUp()) {
            backUp.runBackupFiles();
        }
    }

    public void openAppropriateFragment() {
        if (visibleFragment == SINGLE_SHARED_PROJECT) {
            switchToFragment(SHARED_PROJECTS, null);
        } else if (visibleFragment == PERSONAL_PROJECT) {
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

    public void setupBottomAppBar() {
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_member:
                        inputRequest.setInputRequest(ADD_GROUP_MEMBER);
                        MainActivity.getInputForFragment(MainActivity.visibleFragment);
                        break;
                    case R.id.sign_out:
                        signOut();
                        break;
                    case R.id.view_members:
                        SingleProjectFragment.viewSharedProjectMembers();
                        break;
                }
                return false;
            }
        });
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

    public static void getInputForFragment(final FragmentName requestingFragment) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getAppropriatePrompt());

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

    public static String getAppropriatePrompt() {
        switch (inputRequest.getInputRequest()) {
            case ADD_GROUP_MEMBER:
                return "Enter Member's Email Address";
            case RENAME_PROJECT:
                return "Rename Project";
            case CREATE_NEW_PROJECT:
                return "Enter Project Title";
            case RENAME_TASK:
                return "Rename Task";
            default:
                return "Add a New Task";
        }
    }

    private static void sendInputToVisibleFragment(final FragmentName requestingFragment, final String input) {
        InputRequests.InputRequestType request = inputRequest.getInputRequest();

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
                if (request == RENAME_PROJECT) {
                    SharedProjectsFragment.renameProject(input);
                } else {
                    SharedProjectsFragment.addProject(input);
                }
                break;
            case PERSONAL_PROJECTS:
                if (request == CREATE_NEW_PROJECT) {
                    PersonalProjectsContainerFragment.addProject(input);
                }
                if (request == RENAME_PROJECT) {
                    PersonalProjectsContainerFragment.renameProject(input);
                }
                break;
            case SINGLE_SHARED_PROJECT:
                if (request == ADD_GROUP_MEMBER) {
                    SingleProjectFragment.addMember(input);
                } else if (request == RENAME_TASK) {
                    SingleProjectFragment.renameTask(input);
                } else {
                    SingleProjectFragment.addTask(input);
                }
                break;
            case PERSONAL_PROJECT:
                if (request == RENAME_TASK) {
                    PersonalProjectFragment.renameTask(input);
                } else {
                    //its definitely add new task
                    PersonalProjectFragment.addNewTask(input);
                }
                break;
            default:
                break;
        }

        inputRequest.closeInputRequest();
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
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(DAILY_TASKS);
                        break;
                    case WEEKLY_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(WEEKLY_TASKS);
                        break;
                    case YEARLY_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(YEARLY_TASKS);
                        break;
                    case LONG_TERM_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(LONG_TERM_TASKS);
                        break;
                    case SHARED_PROJECTS:
                        inputRequest.setInputRequest(CREATE_NEW_PROJECT);
                        getInputForFragment(SHARED_PROJECTS);
                        break;
                    case SINGLE_SHARED_PROJECT:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(SINGLE_SHARED_PROJECT);
                        break;
                    case PERSONAL_PROJECT:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(PERSONAL_PROJECT);
                        break;
                    case PERSONAL_PROJECTS:
                        inputRequest.setInputRequest(CREATE_NEW_PROJECT);
                        getInputForFragment(PERSONAL_PROJECTS);
                        break;
                    default:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(DAILY_TASKS);
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

    private void signOut() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        //  auth.signOut();
        // this.finish();
    }

}
