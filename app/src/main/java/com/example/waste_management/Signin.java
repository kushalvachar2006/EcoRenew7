package com.example.waste_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Signin extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleSignInActivity";

    private GoogleSignInClient googleSignInClient;
    private CheckBox showPasswordCheckBox;
    private Button loginButton, signUpButton;
    private TextView forgotPasswordTextView;
    private FirebaseAuth mAuth;

    private ImageButton google, github, linkedin, microsoft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in); // Link to your XML file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        showPasswordCheckBox = findViewById(R.id.show__password);
        loginButton = findViewById(R.id.login_btn);
        signUpButton = findViewById(R.id.sign_up_btn1);
        forgotPasswordTextView = findViewById(R.id.forgot_password);
        google = findViewById(R.id.google);
        microsoft = findViewById(R.id.microsoft);
        github = findViewById(R.id.github);
        linkedin = findViewById(R.id.linkedin);

        // Show/Hide Password Logic
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Login Button Logic
        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty()) {
                usernameEditText.setError("This field cannot be empty");
                Toast.makeText(Signin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
            if(password.isEmpty()){
                Toast.makeText(Signin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                passwordEditText.setError("This field cannot be empty");
            }else {
                loginUser(email, password);
            }
        });

        // Redirect to Sign-Up Page
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Signin.this, SignUp_1.class);
            startActivity(intent);
        });

        // Forgot Password Logic
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Signin.this, Forgot_Password.class);
            startActivity(intent);
        });

        // Google Sign-In Logic
        google.setOnClickListener(v -> signInWithGoogle());
        github.setOnClickListener(v -> githubSignIn());
        microsoft.setOnClickListener(v -> microsoftSignIn());
    }

    // Firebase Authentication Login
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Signin.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home Page or Main Activity
                        startActivity(new Intent(Signin.this, Sample.class));
                        finish();
                    } else {
                        Toast.makeText(Signin.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Google Sign-In Method
    private void signInWithGoogle() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id)) // Ensure this matches the web client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut(); // Clear any cached accounts
        googleSignInClient.revokeAccess();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);
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
                        Toast.makeText(Signin.this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home Page or Main Activity
                        startActivity(new Intent(Signin.this, Sample.class));
                        finish();
                    } else {
                        Log.w(TAG, "SignInWithCredential:failure", task.getException());
                        Toast.makeText(Signin.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Github authentication
    private void githubSignIn() {
        // Create GitHub OAuth Provider instance
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

        // Add custom parameters if needed (e.g., username)
        provider.addCustomParameter("login", usernameEditText.getText().toString());

        // Request additional scopes for GitHub API
        List<String> scopes = new ArrayList<String>() {{
            add("user:email");
        }};
        provider.setScopes(scopes);

        // Check if there's a pending auth result
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // Finish the sign-in process for the user
            pendingResultTask
                    .addOnSuccessListener(authResult -> handleGitHubSignInSuccess(authResult))
                    .addOnFailureListener(e -> {
                        Toast.makeText(Signin.this, "GitHub Authentication Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Start the GitHub sign-in flow
            mAuth.startActivityForSignInWithProvider(Signin.this, provider.build())
                    .addOnSuccessListener(authResult -> handleGitHubSignInSuccess(authResult))
                    .addOnFailureListener(e -> {
                        Toast.makeText(Signin.this, "GitHub Authentication Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Handle successful GitHub sign-in
    private void handleGitHubSignInSuccess(AuthResult authResult) {
        FirebaseUser user = authResult.getUser();
        if (user != null) {
            // Log user details
            Log.d("GitHubAuth", "User ID: " + user.getUid());
            Log.d("GitHubAuth", "Email: " + user.getEmail());

            // Optional: Store user details in Firebase Database
            storeUserInFirebase(user);

            // Navigate to another activity
            Intent intent = new Intent(Signin.this, Sample.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(Signin.this, "Failed to retrieve user details.", Toast.LENGTH_SHORT).show();
        }
    }

    // Store user information in Firebase Realtime Database
    private void storeUserInFirebase(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        String userId = user.getUid();
        String email = user.getEmail();

        User newUser = new User(email);
        userRef.child(userId).setValue(newUser)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseDB", "User stored successfully"))
                .addOnFailureListener(e -> Log.e("FirebaseDB", "Failed to store user", e));
    }

    // Optional: User class for Firebase Database
    public static class User {
        public String email;

        public User(String email) {
            this.email = email;
        }
    }


    //Microsoft authentication
    private void microsoftSignIn() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");

        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    String email = user.getEmail();
                    Toast.makeText(Signin.this, "Microsoft Sign-In Successful: " + email, Toast.LENGTH_SHORT).show();

                    // Redirect to Home or Main Activity
                    startActivity(new Intent(Signin.this, Sample.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Microsoft Sign-In Failed", e);
                    Toast.makeText(Signin.this, "Microsoft Authentication Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




}
