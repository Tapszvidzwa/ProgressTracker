package com.example.tapiwa.todoapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.FileHandler;
import com.example.tapiwa.todoapp.Utils.ProgressTracker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.DAILY_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.LONG_TERM_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.PERSONAL_PROJECTS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.SHARED_PROJECTS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.WEEKLY_TASKS;
import static com.example.tapiwa.todoapp.FragmentFactory.FragmentName.YEARLY_TASKS;
import static com.example.tapiwa.todoapp.home.MainActivity.switchToFragment;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    private static View bottomNavigationSheet;
    private static NavigationView navigationView;
    private static TextView emailAddress;
    private View header;
    private FileHandler fileHandler;
    private ProgressTracker progressTracker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bottomNavigationSheet = inflater.inflate(R.layout.fragment_bottom_nav_drawer, container, false).getRootView();
        navigationView = bottomNavigationSheet.findViewById(R.id.navigation_view);
        header = navigationView.getHeaderView(0);
        emailAddress = header.findViewById(R.id.user_email);
        emailAddress.setText(MainActivity.auth.getCurrentUser().getEmail());
        fileHandler = new FileHandler(getContext());
        progressTracker = new ProgressTracker(getContext(), null);
        return bottomNavigationSheet;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        setupCounters();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    private void setupCounters() {
        TextView[] counterTextView = new TextView[6];
        counterTextView[0] = (TextView) navigationView.getMenu().findItem(R.id.daily_tasks).getActionView();
        counterTextView[1] = (TextView) navigationView.getMenu().findItem(R.id.weekly_tasks).getActionView();
        counterTextView[2] = (TextView) navigationView.getMenu().findItem(R.id.yearly_tasks).getActionView();
        counterTextView[3] = (TextView) navigationView.getMenu().findItem(R.id.long_term_tasks).getActionView();
        counterTextView[4] = (TextView) navigationView.getMenu().findItem(R.id.personal_projects).getActionView();
        counterTextView[5] = (TextView) navigationView.getMenu().findItem(R.id.shared_projects).getActionView();

        String[] count = new String[6];
        count[0] = progressTracker.getUncompleted_daily_tasks();
        count[1] = progressTracker.getUncompleted_weekly_tasks();
        count[2] = progressTracker.getUncompleted_yearly_tasks();
        count[3] = progressTracker.getUncompleted_long_term_tasks();
        count[4] = progressTracker.getUncompleted_personal_projects();
        count[5] = progressTracker.getUncompleted_shared_projects();

        for (int i = 0; i < counterTextView.length; i++) {
            configureCounterView(counterTextView[i], count[i]);
        }
    }

    private void configureCounterView(TextView view, String count) {
        if (count.equals("")) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setText(count);
        }
    }

    private void setupViews() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.daily_tasks:
                        switchToFragment(DAILY_TASKS, new Bundle());
                        break;
                    case R.id.weekly_tasks:
                        switchToFragment(WEEKLY_TASKS, new Bundle());
                        break;
                    case R.id.yearly_tasks:
                        switchToFragment(YEARLY_TASKS, new Bundle());
                        break;
                    case R.id.long_term_tasks:
                        switchToFragment(LONG_TERM_TASKS, new Bundle());
                        break;
                    case R.id.personal_projects:
                        switchToFragment(PERSONAL_PROJECTS, new Bundle());
                        break;
                    case R.id.shared_projects:
                        switchToFragment(SHARED_PROJECTS, new Bundle());
                        break;
                    default:
                        switchToFragment(DAILY_TASKS, new Bundle());
                }
                menuItem.setChecked(true);
                MainActivity.closeBottomSheet();
                return false;
            }
        });
    }
}
