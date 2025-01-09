package com.example.waste_management;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Sample extends AppCompatActivity {
    Button signout;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference mref;

    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        signout=findViewById(R.id.sign_out);

        mAuth=FirebaseAuth.getInstance();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user=mAuth.getCurrentUser();
                    if (mAuth != null) {
                        mAuth.signOut();
                        Intent intent1=new Intent(Sample.this,Signin.class);
                        startActivity(intent1);
                        finish();
                    } else {
                        signoutUser_google();
                        signoutUser_github();
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
                Toast.makeText(Sample.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent2=new Intent(Sample.this, Signin.class);
                startActivity(intent2);
                finish();
            } else {
                Toast.makeText(Sample.this, "Sign-out failed", Toast.LENGTH_SHORT).show();
            }
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleSignInClient.revokeAccess();
            Log.d(TAG, "Cached account cleared");
        }
    }
    //Github signout
    private void signoutUser_github() {
       FirebaseAuth.getInstance().signOut();
//        String accessToken = getStoredGitHubToken(); // Retrieve the stored token
//        if (accessToken != null) {
//
//        }
//
//        mAuth.signOut(); // Firebase logout
//        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
//
//    }
//
//    private String getStoredGitHubToken() {
//        SharedPreferences sharedPreferences = getSharedPreferences("GitHubAuth", MODE_PRIVATE);
//        return sharedPreferences.getString("GitHubAccessToken", null);
//    }
//
//    private void revokeGitHubToken(String clientId, String clientSecret, String token) {
//        OkHttpClient client = new OkHttpClient();
//
//        String url = "https://api.github.com/applications/" + clientId + "/tokens/" + token;
//
//        Request request = new Request.Builder()
//                .url(url)
//                .delete()
//                .header("Authorization", Credentials.basic(clientId, clientSecret))
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.e("GitHubRevoke", "Failed to revoke token", e);
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.d("GitHubRevoke", "Token revoked successfully");
//                } else {
//                    Log.e("GitHubRevoke", "Failed to revoke token: " + response.message());
//                }
//            }
//        });
    }

}