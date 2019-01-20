package com.example.tapiwa.todoapp.login.signIn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Sync.Sync;
import com.example.tapiwa.todoapp.Utils.BackUp;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.login.CreateAccountActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignInActivity extends AppCompatActivity {
    private Button signInBtn, signUpBtn;
    private TextInputEditText emailAddress, password;
    private AppCompatActivity activity;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Util Utils = new Util(this);
    private BackUp backUp;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        backUp = new BackUp(getApplicationContext(), auth.getUid());
        initializeVariables();
        setOnClickListeners();
    }

    private void initializeVariables() {
        signInBtn = findViewById(R.id.sign_in_btn);
        signUpBtn = findViewById(R.id.sign_up_btn);
        emailAddress = findViewById(R.id.login_email_address);
        password = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.logging_progress_bar);
        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Sign In");
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private boolean isEmailAddressValid() {
        if (emailAddress.getText().toString() == null || emailAddress.getText().toString().length() == 0) {
            emailAddress.setError("Invalid email address");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid() {
        if (password.getText().toString() == null || password.getText().toString().length() == 0) {
            password.setError("Invalid password");
            return false;
        }
        return true;
    }

    private void setOnClickListeners() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailAddressValid() && isPasswordValid()) {
                    login(emailAddress.getText().toString(), password.getText().toString());
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });
    }

    private void login(String emailAddress, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Utils.nullifyFirstTimeUserStatus();
                    Intent intent = new Intent(SignInActivity.this, Sync.class);
                    startActivity(intent);
                    activity.finish();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "You entered a wrong email/password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
