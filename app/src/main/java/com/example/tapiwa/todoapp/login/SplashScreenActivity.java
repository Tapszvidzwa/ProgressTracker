package com.example.tapiwa.todoapp.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.example.tapiwa.todoapp.home.MainActivty;
import com.example.tapiwa.todoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



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
       /* Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_logo);
        mAppNameTxtV.startAnimation(myFadeInAnimation); */


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                       mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user =  firebaseAuth.getCurrentUser();

                            if(user != null) {
                                Intent openFrontPage = new Intent(SplashScreenActivity.this, MainActivty.class);
                                startActivity(openFrontPage);
                                thisActivity.finish();
                            } else {
                                Intent openFrontPage = new Intent(SplashScreenActivity.this, CreateAccountActivity.class);
                                startActivity(openFrontPage);
                                thisActivity.finish();
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