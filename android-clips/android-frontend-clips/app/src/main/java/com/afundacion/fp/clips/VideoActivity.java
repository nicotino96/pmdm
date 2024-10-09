package com.afundacion.fp.clips;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    public static final String INTENT_CLIP_ID = "CLIP_ID";
    public static final String INTENT_CLIP_URL = "CLIP_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent(); // Con esto accedemos al Intent que se usó para iniciar la actividad
        int clipId = intent.getIntExtra(VideoActivity.INTENT_CLIP_ID, -1);
        String clipUrl = intent.getStringExtra(VideoActivity.INTENT_CLIP_URL);

        Toast.makeText(this, "videoID: " + clipId, Toast.LENGTH_LONG).show();
    }

}
