package com.afundacion.fp.sessions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * ESTA CLASE SE USA COMO AYUDA PARA UN TEST DE REGISTRO.
 *
 * NO ELIMINES NI EDITES ESTE ARCHIVO.
 */
public class InstrumentationRegisterTestHelperActivity extends AppCompatActivity {
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Class registerClass = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
            Intent intent = new Intent(activity, registerClass);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}