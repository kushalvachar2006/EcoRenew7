package com.example.waste_management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);
        progressBar = findViewById(R.id.progressBar);

        // Reset Password Button Click Logic
        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                emailEditText.setError("This field cannot be empty");
                Toast.makeText(Forgot_Password.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email);
            }
        });
    }

    // Reset Password Method
    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Forgot_Password.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after success
                    } else {
                        Toast.makeText(Forgot_Password.this, "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
