package com.example.tapiwa.todoapp.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tapiwa.todoapp.FragmentFactory.FragmentName;
import com.example.tapiwa.todoapp.FragmentFactory.TasksFragmentFactory;
import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.BackUp;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.InputRequests;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.login.signIn.SignInActivity;
import com.example.tapiwa.todoapp.personalProjects.personalProject.PersonalProjectFragment;
import com.example.tapiwa.todoapp.personalProjects.personalprojectcontainer.PersonalProjectsContainerFragment;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectsFragment;
import com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.SingleProjectFragment;
import com.example.tapiwa.todoapp.tasksDaily.DailyTasksFragment;
import com.example.tapiwa.todoapp.tasksWeekly.WeeklyTasksFragment;
import com.example.tapiwa.todoapp.tasksYearly.YearlyGoalsFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.DAILY_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.LONG_TERM_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.PERSONAL_PROJECT;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.PERSONAL_PROJECTS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.SHARED_PROJECTS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.SINGLE_SHARED_PROJECT;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.WEEKLY_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.YEARLY_TASKS;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.ADD_GROUP_MEMBER;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.CREATE_NEW_PROJECT;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.CREATE_NEW_TASK;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.RENAME_PROJECT;
import static com.example.tapiwa.todoapp.Utils.InputRequests.InputRequestType.RENAME_TASK;


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
        getInputFab = findViewById(R.id.fab);
        visibleFragment = DAILY_TASKS;
        fragmentManager = getSupportFragmentManager();
        activity = this;
        fileHandler = new FileHandler(activity.getApplicationContext());
        bottomSheetDialogFragment = new BottomNavigationDrawerFragment();
        Utils = new Util(this);
        backUp = new BackUp(getApplicationContext(), auth.getUid());
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        setupBottomAppBar();
        updateUpNavigationIcon();
        updateBottomBarMenu();
        initializeFab();
        switchToFragment(DAILY_TASKS, new Bundle());
        updateToolBarName();
        configureWindow();
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
            switchToFragment(SHARED_PROJECTS, new Bundle());
        } else if (visibleFragment == PERSONAL_PROJECT) {
            switchToFragment(PERSONAL_PROJECTS, new Bundle());
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
                        MainActivity.getInputForFragment(MainActivity.visibleFragment, "");
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

    public void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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

    public static void getInputForFragment(final FragmentName requestingFragment, String content) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getAppropriatePrompt());

        int maxLength = 200;
        final EditText newTaskContent = new EditText(activity.getApplicationContext());
        newTaskContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        newTaskContent.setInputType(InputType.TYPE_CLASS_TEXT);
        newTaskContent.setTextColor(Color.BLACK);
        newTaskContent.setVisibility(View.VISIBLE);
        newTaskContent.setText(content);
        builder.setView(newTaskContent);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (newTaskContent.getText().toString().length() > 0) {
                    sendInputToVisibleFragment(requestingFragment, newTaskContent.getText().toString());
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
                if (request == RENAME_PROJECT) {
                    DailyTasksFragment.renameTask(input);
                } else {
                    DailyTasksFragment.addTask(input);
                }
                break;
            case WEEKLY_TASKS:
                if (request == RENAME_PROJECT) {
                    WeeklyTasksFragment.renameTask(input);
                } else {
                    WeeklyTasksFragment.addTask(input);
                }
                break;
            case YEARLY_TASKS:
                if (request == RENAME_PROJECT) {
                    YearlyGoalsFragment.renameTask(input);
                } else {
                    YearlyGoalsFragment.addTask(input);
                }
                break;
            case LONG_TERM_TASKS:
                if (request == RENAME_PROJECT) {
                    DailyTasksFragment.renameTask(input);
                } else {
                    DailyTasksFragment.addTask(input);
                }
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
        TasksFragmentFactory fragmentsFactory = new TasksFragmentFactory();
        androidx.fragment.app.Fragment fragment = fragmentsFactory.getFragment(fragmentName);
        mergeFragmentBundles(fragment, bundle);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_holder, fragment)
                .commit();

        updateToolBarName();
        updateUpNavigationIcon();
        updateBottomBarMenu();
    }

    public static void mergeFragmentBundles(Fragment fragment, Bundle bundle) {
        Bundle fragmentBundle = fragment.getArguments();
        if (fragmentBundle == null) {
            fragmentBundle = new Bundle();
        }
        fragmentBundle.putAll(bundle);
        fragment.setArguments(fragmentBundle);
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

    private void initializeFab() {
        getInputFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (visibleFragment) {
                    case DAILY_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(DAILY_TASKS, "");
                        break;
                    case WEEKLY_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(WEEKLY_TASKS, "");
                        break;
                    case YEARLY_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(YEARLY_TASKS, "");
                        break;
                    case LONG_TERM_TASKS:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(LONG_TERM_TASKS, "");
                        break;
                    case SHARED_PROJECTS:
                        inputRequest.setInputRequest(CREATE_NEW_PROJECT);
                        getInputForFragment(SHARED_PROJECTS, "");
                        break;
                    case SINGLE_SHARED_PROJECT:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(SINGLE_SHARED_PROJECT, "");
                        break;
                    case PERSONAL_PROJECT:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(PERSONAL_PROJECT, "");
                        break;
                    case PERSONAL_PROJECTS:
                        inputRequest.setInputRequest(CREATE_NEW_PROJECT);
                        getInputForFragment(PERSONAL_PROJECTS, "");
                        break;
                    default:
                        inputRequest.setInputRequest(CREATE_NEW_TASK);
                        getInputForFragment(DAILY_TASKS, "");
                }
            }
        });
    }

    public static void closeBottomSheet() {
        bottomSheetDialogFragment.dismiss();
    }

    private void signOut() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();
    }
}
