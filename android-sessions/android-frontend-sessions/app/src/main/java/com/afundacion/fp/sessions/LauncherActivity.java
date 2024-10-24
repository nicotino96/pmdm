package com.afundacion.fp.sessions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recuperamos el nombre de usuario de las preferencias
        SharedPreferences preferences = getSharedPreferences("SESSIONS_APP_PREFS", MODE_PRIVATE);
        String username = preferences.getString("VALID_USERNAME", null); // null será el valor por defecto

        // Si el usuario NO se ha logueado, el valor es 'null' por defecto
        // ¡Vamos a iniciar la pantalla de Login!
        if (username == null) {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);

            // Si el usuario SÍ se ha logueado, ya disponemos de su nombre de usuario
            // ¡Vamos a iniciar la pantalla principal!
        } else {
            Intent statusActivity = new Intent(this, StatusActivity.class);
            startActivity(statusActivity);
        }
    }
}


