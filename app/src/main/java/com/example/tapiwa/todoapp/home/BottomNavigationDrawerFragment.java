package com.example.tapiwa.todoapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tapiwa.todoapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.TintableBackgroundView;

import static com.example.tapiwa.todoapp.home.MainActivity.switchToFragment;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    private static View bottomNavigationSheet;
    private static NavigationView navigationView;
    private static TextView emailAddress;
    private View header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bottomNavigationSheet = inflater.inflate(R.layout.fragment_bottom_nav_drawer, container, false).getRootView();
        navigationView = bottomNavigationSheet.findViewById(R.id.navigation_view);
        header = navigationView.getHeaderView(0);
        emailAddress = header.findViewById(R.id.user_email);
        emailAddress.setText(MainActivity.auth.getCurrentUser().getEmail());
        return bottomNavigationSheet;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }


    private void setupViews() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.daily_tasks:
                        switchToFragment(MainActivity.FragmentName.DAILY_TASKS, null);
                        break;
                    case R.id.weekly_tasks:
                        switchToFragment(MainActivity.FragmentName.WEEKLY_TASKS, null);
                        break;
                    case R.id.yearly_goals:
                        switchToFragment(MainActivity.FragmentName.YEARLY_TASKS, null);
                        break;
                    case R.id.five_year_goals:
                        switchToFragment(MainActivity.FragmentName.LONG_TERM_TASKS, null);
                        break;
                    case R.id.personal_projects:
                        switchToFragment(MainActivity.FragmentName.PERSONAL_PROJECTS, null);
                        break;
                    case R.id.shared_projects:
                        switchToFragment(MainActivity.FragmentName.SHARED_PROJECTS, null);
                        break;
                    default:
                        switchToFragment(MainActivity.FragmentName.DAILY_TASKS, null);
                }
                menuItem.setChecked(true);
                MainActivity.closeBottomSheet();
                return false;
            }
        });
    }
}
