package com.example.waste_management;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home_Page extends AppCompatActivity {
    Button signout;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mref;

    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        signout = findViewById(R.id.sign_out);

        mAuth = FirebaseAuth.getInstance();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (mAuth != null) {
                    mAuth.signOut();
                    Intent intent1 = new Intent(Home_Page.this, Choice_Activity.class);
                    startActivity(intent1);
                    finish();
                } else {
                    signoutUser_google();
                }
            }
        });
    }

    //Google signout
    private void signoutUser_google() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Google Sign-Out Successful");
            } else {
                Log.e(TAG, "Google Sign-Out Failed");
            }
        });
        googleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Home_Page.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(Home_Page.this, Choice_Activity.class);
                startActivity(intent2);
                finish();
            } else {
                Toast.makeText(Home_Page.this, "Sign-out failed", Toast.LENGTH_SHORT).show();
            }
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleSignInClient.revokeAccess();
            Log.d(TAG, "Cached account cleared");
        }
    }
}

