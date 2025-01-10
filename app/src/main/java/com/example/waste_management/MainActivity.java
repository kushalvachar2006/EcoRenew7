package com.example.waste_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    FirebaseAuth mauth;
    ImageView imageView;
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.logo1);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        Animation animation;

        mauth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("users");
        firebaseUser=mauth.getCurrentUser();
        if(firebaseUser!=null){
            updateUI(firebaseUser);
        }
        else{
            navigatetologin();
        }
    }

    private void navigatetologin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Choice_Activity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imageView, "sharedImage");
                startActivity(intent, options.toBundle());
                finish();
            }
        }, 1000);

    }

    private void updateUI(FirebaseUser firebaseUser) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Home_Page_user.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imageView, "sharedImage");
                startActivity(intent, options.toBundle());
                finish();
            }
        }, 1000);

    }
}