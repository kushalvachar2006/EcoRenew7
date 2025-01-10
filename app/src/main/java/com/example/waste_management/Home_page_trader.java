package com.example.waste_management;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home_page_trader extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button signoutbtn;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_trader);

        signoutbtn=findViewById(R.id.signout);
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signoutbtn.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // Sign out from Firebase
                mAuth.signOut();
                navigateToChoiceActivity();
            } else {
                // Sign out from Google
                signoutUserGoogle();
            }
        });
    }
    // Sign out from Google
    private void signoutUserGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Google Sign-Out Successful");
            } else {
                Log.e(TAG, "Google Sign-Out Failed");
            }
        });

        googleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Home_page_trader.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                navigateToChoiceActivity();
            } else {
                Toast.makeText(Home_page_trader.this, "Sign-out failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to Choice Activity
    private void navigateToChoiceActivity() {
        Intent intent = new Intent(Home_page_trader.this, Choice_Activity.class);
        startActivity(intent);
        finish();
    }
}