package com.example.waste_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp_1 extends AppCompatActivity {
    boolean pass=false;
    EditText name,email,username,password;
    String user_name,user_email,user_username,user_password;
    Button next;
    CheckBox password_show;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_1);

        name=findViewById(R.id.fullname);
        email=findViewById(R.id.email_id);
        username=findViewById(R.id.username1);
        password=findViewById(R.id.password1);
        next=findViewById(R.id.next_btn);
        password_show=findViewById(R.id.show_password);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_name = name.getText().toString().trim();
                user_email = email.getText().toString().trim();
                user_username = username.getText().toString().trim();
                user_password = password.getText().toString().trim();
                if (TextUtils.isEmpty(user_name)) {
                    name.setError("This field cannot be empty");
                }
                if (TextUtils.isEmpty(user_email)) {
                    email.setError("This field cannot be empty");
                }
                if (TextUtils.isEmpty(user_username)) {
                    username.setError("This field cannot be empty");
                }
                if (TextUtils.isEmpty(user_password)) {
                    password.setError("This field cannot be empty");
                } else {
                    name.setError(null);
                    email.setError(null);
                    username.setError(null);
                    password.setError(null);
                    registerUser(user_email,user_password);
                    //registerUser1();
                }
            }
        });
        password_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start=password.getSelectionStart();
                int end=password.getSelectionEnd();
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                password.setSelection(start,end);
            }
        });



    }
    private void registerUser(String user_email, String user_password) {
        mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User successfully authenticated, now register user details in the database
                    registerUser1();

                    Intent intent = new Intent(SignUp_1.this, sign_up2.class);
                    startActivity(intent);
                    finish();
                } else {
                    email.setError("Invalid email");
                    Toast.makeText(SignUp_1.this, "Authentication failed:User exits", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser1() {
        myRef=database.getReference("users");
        User user=new User(user_name,user_email,user_password);
        myRef.child(user_username).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUp_1.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignUp_1.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}