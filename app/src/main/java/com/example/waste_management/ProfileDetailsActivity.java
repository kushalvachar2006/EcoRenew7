package com.example.waste_management;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileDetailsActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName, userEmail, userPhone,personal1,personal2,phno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        // Initialize Views
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        personal1=findViewById(R.id.personal_name);
        personal2=findViewById(R.id.personal_email);
        phno=findViewById(R.id.user_phone);

        // Load Profile Data
        loadProfileData();
    }

    private void loadProfileData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (firebaseUser != null) {
            Log.d("ProfileDetailsActivity", "Firebase User: " + firebaseUser.getDisplayName() + ", " + firebaseUser.getEmail());
            userName.setText(firebaseUser.getDisplayName());
            userEmail.setText(firebaseUser.getEmail());
            personal1.setText((firebaseUser.getDisplayName()));
            personal2.setText((firebaseUser.getEmail()));
            phno.setText(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "Not Linked");
            Glide.with(this).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.user).into(profileImage);
        } else if (googleAccount != null) {
            Log.d("ProfileDetailsActivity", "Google User: " + googleAccount.getDisplayName() + ", " + googleAccount.getEmail());
            userName.setText(googleAccount.getDisplayName());
            userEmail.setText(googleAccount.getEmail());
            personal1.setText((firebaseUser.getDisplayName()));
            personal2.setText((firebaseUser.getEmail()));
            phno.setText(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "Not Linked");
            Glide.with(this).load(googleAccount.getPhotoUrl()).placeholder(R.drawable.user).transform(new RoundedCornersTransformation(100,0)).into(profileImage);
        } else {
            Log.e("ProfileDetailsActivity", "No user is signed in.");
            userName.setText("Guest");
            userEmail.setText("Not logged in");
            personal1.setText((firebaseUser.getDisplayName()));
            personal2.setText((firebaseUser.getEmail()));
            phno.setText(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "Not Linked");

            profileImage.setImageResource(R.drawable.user);
        }
    }

}
