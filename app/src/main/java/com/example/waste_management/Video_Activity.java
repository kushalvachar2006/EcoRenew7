package com.example.waste_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class Video_Activity extends AppCompatActivity {
    Button skipbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        skipbtn=findViewById(R.id.skip);
        VideoView videoView = findViewById(R.id.video_view);

        // Get the URI of the video in the raw folder
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);

        // Set the video URI
        videoView.setVideoURI(videoUri);

        // Add media controls (play, pause, etc.)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start the video
        videoView.start();
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Video_Activity.this,Home_Page_user.class);
                startActivity(intent);
                finish();
            }
        });
    }
}