package com.example.tapiwa.todoapp.navigation;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationRobotTest {

    @Rule
    public final ActivityTestRule<NavigationController> mActivityRule = new ActivityTestRule<>(NavigationController.class);

    @Test
    public void openBottomFragment() {
        NavigationRobot navigationRobot = new NavigationRobot();
        navigationRobot.openFragment();
    }
}
