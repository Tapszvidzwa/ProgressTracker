package com.example.tapiwa.todoapp.login;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.tapiwa.todoapp.InitializeApp.InitializeApp;
import com.example.tapiwa.todoapp.home.MainActivty;
import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class CreateAccountActivity extends AppCompatActivity  {

    private TextInputEditText emailEdtTxt, usernameEdtTxt, passwordEdtTxt, passwordConfirmEditTxt;
    public Button createAccountBtn;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private RelativeLayout mBackground;
    private ProgressBar mProgressBar;
    private String TAG = "CREATE_ACCOUNT_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tapiwa.todoapp.R.layout.create_account_activity);

        emailEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.email_address_EdtTxt);
        usernameEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.username_EdtTxt);
        mBackground = findViewById(com.example.tapiwa.todoapp.R.id.create_account_bkgrnd);
        mProgressBar = findViewById(com.example.tapiwa.todoapp.R.id.login_progress_circle);
        passwordEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.password_EdtTxt);
        passwordConfirmEditTxt = findViewById(com.example.tapiwa.todoapp.R.id.password_confirm_EdtTxt);
        createAccountBtn = findViewById(com.example.tapiwa.todoapp.R.id.create_account_Btn);
        mToolbar = findViewById(com.example.tapiwa.todoapp.R.id.create_new_account_toolbar);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Create Account");
        mToolbar.setTitleTextColor(Color.WHITE);

        createAccountBtn.setActivated(true);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "butn clicked");
                if(checkValidity()) {
                    attemptLogin(passwordEdtTxt.getText().toString().trim(),
                            emailEdtTxt.getText().toString().trim());
                }
            }
        });

    }

    private boolean checkValidity() {

        mProgressBar.setVisibility(View.VISIBLE);
        mBackground.getBackground().setAlpha(100);

        if(emailEdtTxt.getText().toString().trim().length() == 0) {
            //Todo check all other things that make a valid email
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if(passwordEdtTxt.getText().toString().trim().length() == 0) {
            //Todo: make sure the password entered is strong enough
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if(usernameEdtTxt.getText().toString().trim().length() == 0) {
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if(!passwordConfirmEditTxt.getText().toString().trim()
                .equals(passwordEdtTxt.getText().toString().trim())) {
            passwordConfirmEditTxt.setError("You entered different passwords");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        return true;

    }

    private void dimBackground() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
    }

   private void attemptLogin(String password, String email) {

        dimBackground();

       mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()) {
                   mProgressBar.setVisibility(View.INVISIBLE);
                   mBackground.getBackground().setAlpha(0);
                   //TODO: create a loading circle bar here
                   InitializeApp initializeApp = new InitializeApp(getApplicationContext());
                   initializeApp.createFiles();
                   Intent intent = new Intent(CreateAccountActivity.this, MainActivty.class);
                   startActivity(intent);
                   CreateAccountActivity.this.finish();
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               mProgressBar.setVisibility(View.INVISIBLE);
               mBackground.getBackground().setAlpha(0);
               Toasty.error(getApplicationContext(), "An error occured in signing up, try again");
           }
       });
       //Todo add on failure listener and inform user using a toast if error happens
    }


}
