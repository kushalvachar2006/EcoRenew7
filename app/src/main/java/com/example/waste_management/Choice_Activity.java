package com.example.waste_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Choice_Activity extends AppCompatActivity {
    Button user,trader;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        user=findViewById(R.id.userbtn);
        trader=findViewById(R.id.traderbtn);
        img=findViewById(R.id.choice_img);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Choice_Activity.this, Signin_user.class);
                startActivity(intent);
                finish();
                saveUserChoice("User");

            }
        });
        trader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Choice_Activity.this, Signin_trader.class);
                startActivity(intent);
                finish();
                saveUserChoice("Trader");
            }
        });


    }
    private void saveUserChoice(String choice) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserChoice", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", choice);
        editor.apply();
    }
}