package com.example.waste_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class sign_up2 extends AppCompatActivity {

    EditText mobile, otp;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String mobile_num;
    Button next, signup;

    FirebaseAuth mAuth;
    String verificationId; // To store the OTP verification ID from Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_2);

        // Initialize UI Components
        mobile = findViewById(R.id.phone_number);
        otp = findViewById(R.id.otp);
        next = findViewById(R.id.next_btn2);
        signup = findViewById(R.id.sign_up_btn2);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Next Button - Send OTP
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile_num = mobile.getText().toString().trim();
                if (TextUtils.isEmpty(mobile_num)) {
                    mobile.setError("This field cannot be empty");
                } else if (mobile_num.length() < 10 || mobile_num.length() > 10) {
                    Toast.makeText(sign_up2.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();

                } else {
                    mobile.setError(null);
                    Toast.makeText(sign_up2.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    signup.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    otp.setVisibility(View.VISIBLE);
                    //Send otp method
                    String phone = "+91" + mobile.getText().toString();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(otp.getText().toString())) {
                    Toast.makeText(sign_up2.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    //verifyCode(otp.getText().toString());
                    Toast.makeText(sign_up2.this, "Sign-up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(sign_up2.this, Sample.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
