package com.example.waste_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Signin_user extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleSignInActivity";

    private EditText usernameEditText, passwordEditText;
    private CheckBox showPasswordCheckBox;
    private Button loginButton, signUpButton, googleButton;
    private TextView forgotPasswordTextView;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        showPasswordCheckBox = findViewById(R.id.show__password);
        loginButton = findViewById(R.id.login_btn);
        signUpButton = findViewById(R.id.sign_up_btn1);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        googleButton = findViewById(R.id.google);

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Hide password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Move the cursor to the end of the text after changing input type
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        // Google Sign-In Configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("743380628213-nfsldnioocacblcmqr44vnl3qgov582r.apps.googleusercontent.com") // Replace with your Web Client ID if needed
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut(); // Clear any cached accounts
        googleSignInClient.revokeAccess();

        // Google Sign-In Button Logic
        googleButton.setOnClickListener(v -> signInWithGoogle());

        // Login Button Logic
        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Signin_user.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            loginUser(email, password);
        });

        // Sign-Up Redirect
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Signin_user.this, SignUp_1_user.class);
            startActivity(intent);
        });

        // Forgot Password Redirect
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Signin_user.this, Forgot_Password.class);
            startActivity(intent);
        });
    }

    // Login with Firebase Email & Password
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Signin_user.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home Page or Main Activity
                        startActivity(new Intent(Signin_user.this, Video_Activity.class));
                        finish();
                    } else {
                        Toast.makeText(Signin_user.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Signin_user.this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home Page or Main Activity
                        startActivity(new Intent(Signin_user.this, Video_Activity.class));
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Signin_user.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
