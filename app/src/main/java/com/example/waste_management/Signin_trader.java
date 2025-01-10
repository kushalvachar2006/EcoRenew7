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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signin_trader extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleSignInActivity";

    private EditText usernameEditText, passwordEditText, addressEditText;
    private CheckBox showPasswordCheckBox;
    private Button loginButton, signUpButton, googleButton;
    private TextView forgotPasswordTextView;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_trader);

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

        // Show Password Logic
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Google Sign-In Configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("743380628213-nfsldnioocacblcmqr44vnl3qgov582r.apps.googleusercontent.com") // Replace with your Web Client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        googleSignInClient.revokeAccess();

        // Google Sign-In Button
        googleButton.setOnClickListener(v -> signInWithGoogle());

        // Login Button
        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();


            if (email.isEmpty() || password.isEmpty() ) {
                Toast.makeText(Signin_trader.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        // Sign-Up Button
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Signin_trader.this, SignUp_1_trader.class);
            startActivity(intent);
        });

        // Forgot Password Button
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Signin_trader.this, Forgot_Password.class);
            startActivity(intent);
        });
    }

    // Login User and Store Address
    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Signin_trader.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adding logs to check values
        Log.d("Login", "Email: " + email + " Password: " + password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d("Login", "Login Successful!");

                        Toast.makeText(Signin_trader.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Trader Details Page
                        startActivity(new Intent(Signin_trader.this, Trader_Details.class));
                        finish();
                    } else {
                        Log.e("Login", "Authentication Failed: " + task.getException().getMessage());
                        Toast.makeText(Signin_trader.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Store Trader Details in Firebase
    private void storeTraderDetails(String traderId, String traderAddress) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("traders").child(traderId);

        // Create Trader object
        Trader trader = new Trader(traderId, traderAddress);

        ref.setValue(trader)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save details: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                        Toast.makeText(Signin_trader.this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();

                        // Redirect to Home Page or Main Activity
                        startActivity(new Intent(Signin_trader.this, Trader_Details.class));
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Signin_trader.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
