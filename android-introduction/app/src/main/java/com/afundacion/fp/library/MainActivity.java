package com.afundacion.fp.library;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(context,"MainActivity en segundo plano",Toast.LENGTH_LONG).show();
    }

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "La primera tostada del día", Toast.LENGTH_SHORT).show();
        OnClickHandler myHandler = new OnClickHandler(this);
        Button myButton = findViewById(R.id.toastButton);
        myButton.setOnClickListener(myHandler);
        Button otherButton = findViewById(R.id.bottomButton);
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Mostrando otra actividad...", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(context, MonstersActivity.class);
                context.startActivity(myIntent);

            }
        });


    }


}