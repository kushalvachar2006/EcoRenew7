package com.example.waste_management;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Home_Page_user extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout navDrawer;
    private Button signout, profileButton,click;
    private ImageButton profile,camera1;
    private ImageView profileImage;
    private TextView userName, userEmail;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_user);
        click=findViewById(R.id.clickhere);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Views
        profile = findViewById(R.id.navbar_user_photo); // Profile icon in navbar
        drawerLayout = findViewById(R.id.drawer_layout); // DrawerLayout
        navDrawer = findViewById(R.id.navDrawer); // Navigation Drawer
        profileButton = findViewById(R.id.profile_button); // Profile button in drawer
        signout = findViewById(R.id.sign_out); // Sign-out button
        profileImage = findViewById(R.id.profile_image); // Profile picture in the drawer
        userName = findViewById(R.id.user_name); // User's name
        userEmail = findViewById(R.id.user_email); // User's email
        camera1=findViewById(R.id.camera);

        RecyclerView recyclerView = findViewById(R.id.leaderboard_recycler_view);
        TextView emptyMessage = findViewById(R.id.empty_message);

        // Initialize RecyclerView


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = "com.example.object_detection"; // Replace with the package name of the other app

                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);  // Launch the other app
                } else {
                    // Handle case where the app is not installed
                    Toast.makeText(Home_Page_user.this, "App not found", Toast.LENGTH_SHORT).show();
                }
            }

        });
        // Open Drawer when Profile Icon is clicked
        profile.setOnClickListener(v -> toggleDrawer());

        // Profile Button Click Listener
        profileButton.setOnClickListener(v -> {
            Toast.makeText(Home_Page_user.this, "Profile button clicked!", Toast.LENGTH_SHORT).show();
            // Navigate to Profile Details Activity
            Intent intent = new Intent(Home_Page_user.this, ProfileDetailsActivity.class);
            startActivity(intent);
        });

        // Sign-Out Button Listener
        signout.setOnClickListener(v -> {
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

        // Load user data into the navigation drawer
        loadUserData();
    }

    // Toggle Drawer
    private void toggleDrawer() {

        if (drawerLayout.isDrawerOpen(navDrawer)) {
            drawerLayout.closeDrawer(navDrawer);
        } else {
            drawerLayout.openDrawer(navDrawer);
        }
    }

    // Load User Data into Drawer
    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (firebaseUser != null) {
            // Firebase user data
            userName.setText(firebaseUser.getDisplayName());
            userEmail.setText(firebaseUser.getEmail());
            Glide.with(this).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.user).into(profileImage);
        } else if (googleAccount != null) {
            // Google user data
            userName.setText(googleAccount.getDisplayName());
            userEmail.setText(googleAccount.getEmail());
            Glide.with(this).load(googleAccount.getPhotoUrl()).placeholder(R.drawable.user).into(profileImage);
        } else {
            // Guest user
            userName.setText("Guest");
            userEmail.setText("Not logged in");
            profileImage.setImageResource(R.drawable.user);
        }
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
                Toast.makeText(Home_Page_user.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                navigateToChoiceActivity();
            } else {
                Toast.makeText(Home_Page_user.this, "Sign-out failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to Choice Activity
    private void navigateToChoiceActivity() {
        Intent intent = new Intent(Home_Page_user.this, Choice_Activity.class);
        startActivity(intent);
        finish();
    }
}
