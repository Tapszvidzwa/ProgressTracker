package com.example.tapiwa.todoapp.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.tapiwa.todoapp.InitializeApp.InitializeApp;
import com.example.tapiwa.todoapp.R;
import com.example.tapiwa.todoapp.Utils.Util;
import com.example.tapiwa.todoapp.home.MainActivity;
import com.example.tapiwa.todoapp.login.signIn.SignInActivity;
import com.example.tapiwa.todoapp.sharedProjects.SharedProjectReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.todoapp.Utils.Constants.USERS_DB_PATH;

public class CreateAccountActivity extends AppCompatActivity {

    public Button createAccountBtn, signIn;
    private TextInputEditText emailEdtTxt, usernameEdtTxt, passwordEdtTxt, passwordConfirmEditTxt;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private RelativeLayout mBackground;
    private ProgressBar mProgressBar;
    private String TAG = "CREATE_ACCOUNT_ACTIVITY";
    private Util Utils = new Util(this);
    private AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tapiwa.todoapp.R.layout.activity_create_account);

        emailEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.email_address_EdtTxt);
        usernameEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.username_EdtTxt);
        mBackground = findViewById(com.example.tapiwa.todoapp.R.id.create_account_bkgrnd);
        mProgressBar = findViewById(com.example.tapiwa.todoapp.R.id.login_progress_circle);
        passwordEdtTxt = findViewById(com.example.tapiwa.todoapp.R.id.password_EdtTxt);
        signIn = findViewById(R.id.sign_up_from_create_account_btn);
        passwordConfirmEditTxt = findViewById(com.example.tapiwa.todoapp.R.id.password_confirm_EdtTxt);
        createAccountBtn = findViewById(com.example.tapiwa.todoapp.R.id.create_account_Btn);
        activity = this;

        mToolbar = findViewById(com.example.tapiwa.todoapp.R.id.create_new_account_toolbar);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Create Account");
        mToolbar.setTitleTextColor(Color.WHITE);

        createAccountBtn.setActivated(true);
        setButtonClickListeners();

        new InitializeApp(getApplicationContext()).execute(true);
    }

    private void setButtonClickListeners() {
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserCredentialsValid()) {
                    attemptLogin(
                            passwordEdtTxt.getText().toString().trim(),
                            emailEdtTxt.getText().toString().trim(),
                            usernameEdtTxt.getText().toString().trim());
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, SignInActivity.class);
                startActivity(intent);
                activity.finish();

            }
        });
    }

    private boolean isUserCredentialsValid() {

        mProgressBar.setVisibility(View.VISIBLE);
        mBackground.getBackground().setAlpha(100);

        if (emailEdtTxt.getText().toString().trim().length() == 0) {
            //Todo check all other things that make a valid email
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if (passwordEdtTxt.getText().toString().trim().length() == 0) {
            //Todo: make sure the password entered is strong enough
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if (usernameEdtTxt.getText().toString().trim().length() == 0) {
            emailEdtTxt.setError("Invalid email");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        if (!passwordConfirmEditTxt.getText().toString().trim()
                .equals(passwordEdtTxt.getText().toString().trim())) {
            passwordConfirmEditTxt.setError("You entered different passwords");
            mProgressBar.setVisibility(View.INVISIBLE);
            mBackground.getBackground().setAlpha(0);
            return false;
        }

        return true;

    }

    //Todo: fix this method, not yet working as expected
    private void dimBackground() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
    }


    private void registerUserToFirestore(String uid, String username, String email) {

        User user = new User();
        user.setEmail(email);
        user.setUserName(username);
        user.setUid(uid);
        user.setDailyProjects("");
        user.setWeeklyProjects("");
        user.setLongTermProjects("");
        user.setYearlyProjects("");
        user.setSharedProjectReferenceKeys(new ArrayList<SharedProjectReference>());
        user.setPersonalProjects("");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document(USERS_DB_PATH + uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });


        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getUserName())
                .setPhotoUri(null)
                .build();

        fbUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void attemptLogin(String password, final String email, final String username) {
        dimBackground();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Utils.setFirstTimeUser();
                    String uid = mAuth.getCurrentUser().getUid();
                    registerUserToFirestore(uid, username, email);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mBackground.getBackground().setAlpha(0);
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
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
    }
}
