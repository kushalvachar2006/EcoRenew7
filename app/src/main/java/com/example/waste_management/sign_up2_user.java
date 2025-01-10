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

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class sign_up2_user extends AppCompatActivity {

    EditText mobile, otp;
    Long timeout = 60L;
    PhoneAuthProvider.ForceResendingToken ResendingToken;
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
                    Toast.makeText(sign_up2_user.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    mobile.setError(null);
                    signup.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    otp.setVisibility(View.VISIBLE);
                    // Send OTP method
                    String phone = "+91" + mobile.getText().toString();
                    sendotp(phone);
                }
            }
        });

        // Sign-up Button - Manual OTP Verification
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = otp.getText().toString().trim();
                if (TextUtils.isEmpty(enteredOtp)) {
                    otp.setError("Please enter OTP");
                } else {
                    // Verify the entered OTP
                    verifyCode(enteredOtp);
                }
            }
        });
    }

    // Send OTP to the user's phone number
    void sendotp(String phno) {
        PhoneAuthOptions.Builder builder = new PhoneAuthOptions.Builder(mAuth)
                .setPhoneNumber(phno)
                .setTimeout(timeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Automatically sign in the user when OTP is detected
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(sign_up2_user.this, "Failed to authenticate", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        ResendingToken = forceResendingToken;
                        Toast.makeText(sign_up2_user.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    // Verify OTP entered by the user
    void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Sign in the user with the phoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // OTP is verified successfully, proceed to sign-up
                        Toast.makeText(sign_up2_user.this, "Sign-up Successful", Toast.LENGTH_SHORT).show();

                        // Store user data in Firebase Database (if required)
                        String userId = mAuth.getCurrentUser().getUid();
                        myRef.child(userId).setValue(mobile_num);

                        // Navigate to the home page
                        Intent intent = new Intent(sign_up2_user.this, Video_Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Verification failed
                        Toast.makeText(sign_up2_user.this, "Verification failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
