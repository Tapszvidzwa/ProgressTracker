package com.example.tapiwa.todoapp.sharedProjects.SingleProjectFragment.projectmembers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectModel;

public class ProjectMembersActivity  extends AppCompatActivity{

    private ListView listView;
    private Toolbar toolbar;
    private SharedProjectModel projectModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_members);
        projectModel = (SharedProjectModel) getIntent().getSerializableExtra("projectModel");
        initializeViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        displayMembers();
        configureWindow();
    }

    private void initializeViews() {
        listView = findViewById(R.id.project_members);
        toolbar = findViewById(R.id.project_members_toolbar);
        toolbar.setTitle("Project Members");
    }

    private void displayMembers() {
        ProjectMembersAdapter adapter = new ProjectMembersAdapter(getApplicationContext(),
                R.layout.item_project_member,
                projectModel.getMemberNames(),
                projectModel.getMemberEmails());
        listView.setAdapter(adapter);
    }

    public void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}