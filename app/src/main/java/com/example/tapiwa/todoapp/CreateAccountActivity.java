package com.example.tapiwa.todoapp;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputEditText emailEdtTxt, usernameEdtTxt, passwordEdtTxt;
    private Button createAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        emailEdtTxt = findViewById(R.id.email_address_EdtTxt);
        usernameEdtTxt = findViewById(R.id.username_EdtTxt);
        passwordEdtTxt = findViewById(R.id.password_EdtTxt);
        createAccountBtn = findViewById(R.id.create_account_Button);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }



    private void checkValidity() {

    }






}
