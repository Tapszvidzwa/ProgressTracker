package com.example.tapiwa.todoapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.tapiwa.zimsecprep.Activities.GlobalActivities.FrontPageActivity;
import com.example.tapiwa.zimsecprep.LoadFiles.LoadFiles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class SplashScreenActivity extends Activity {

    private TextView mAppNameTxtV;
    public FirebaseAuth mAuth;
    private Activity thisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        mAppNameTxtV = findViewById(R.id.splash_screen_logo);
        mAuth = FirebaseAuth.getInstance();
        thisActivity = this;

        //app name fade in
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mAppNameTxtV, "alpha", 0f, 1f);
        fadeIn.setDuration(2800);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();

        //app logo fade in
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_logo);
        mAppNameTxtV.startAnimation(myFadeInAnimation);


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2800);
                       mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user =  firebaseAuth.getCurrentUser();
                            if(user != null) {
                                Intent openFrontPage = new Intent(SplashScreenActivity.this,FrontPageActivity.class);
                                startActivity(openFrontPage);
                                thisActivity.finish();
                            } else {

                                Intent openFrontPage = new Intent(SplashScreenActivity.this,FrontPageActivity.class);
                                startActivity(openFrontPage);
                                thisActivity.finish();

                              /*Intent openRegistrationPage = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                startActivity(openRegistrationPage);
                                thisActivity.finish(); */
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }






}