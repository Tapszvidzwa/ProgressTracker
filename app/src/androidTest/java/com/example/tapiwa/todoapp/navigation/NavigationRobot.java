package com.example.tapiwa.todoapp.navigation;

import android.view.Gravity;

import com.example.tapiwa.todoapp.R;

import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class NavigationRobot {

    public NavigationRobot() {
    }

    public NavigationRobot openFragment() {
        onView(withId(R.id.bar)).
                check(matches(isDisplayed()));
                //.check(selectedDescendantsMatch(withId(android.R.id.home), null));
        return this;
    }
}
