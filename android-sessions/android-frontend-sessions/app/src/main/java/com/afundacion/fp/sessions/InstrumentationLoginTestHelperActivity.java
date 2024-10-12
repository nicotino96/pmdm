package com.afundacion.fp.sessions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ESTA CLASE SE USA COMO AYUDA PARA UN TEST DE LOGIN.
 *
 * NO ELIMINES NI EDITES ESTE ARCHIVO.
 */
public class InstrumentationLoginTestHelperActivity extends AppCompatActivity {
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Class registerClass = Class.forName("com.afundacion.fp.sessions.LoginActivity");
            Intent intent = new Intent(activity, registerClass);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}