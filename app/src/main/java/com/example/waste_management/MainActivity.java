package com.example.waste_management;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    FirebaseAuth mauth;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.logo1);
        Animation animation;

        mauth=FirebaseAuth.getInstance();
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
                Intent intent = new Intent(MainActivity.this, Signin.class);
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
                Intent intent = new Intent(MainActivity.this, Sample.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imageView, "sharedImage");
                startActivity(intent, options.toBundle());
                finish();
            }
        }, 1000);

    }
    }
