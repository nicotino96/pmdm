package com.afundacion.fp.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class StatusActivity extends AppCompatActivity {
    private RequestQueue queue;
    private Context context=this;
    private TextView textViewUserStatus;
    private FloatingActionButton buttonPutStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        queue = Volley.newRequestQueue(this);
        obtainStatus();
        textViewUserStatus=findViewById(R.id.cargando);
        buttonPutStatus = findViewById(R.id.button_open_dialog);
        buttonPutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
                myBuilder.setPositiveButton("Cambiar estado", null); // Esto añade un botón al diálogo
                AlertDialog myDialog = myBuilder.create(); // Esta línea es como 'new AlertDialog'
                myDialog.show();

            }
        });



    }
    private void obtainStatus() {
        SharedPreferences preferences = getSharedPreferences("SESSIONS_APP_PREFS", MODE_PRIVATE);
        String username = preferences.getString("VALID_USERNAME", null); // null será el valor por defecto

        JsonObjectRequestAuthenticated request = new JsonObjectRequestAuthenticated(
                Request.Method.GET,
                Server.name + "/users/" + username + "/status",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Estado obtenido", Toast.LENGTH_LONG).show();
                        try {
                            textViewUserStatus.setText(response.getString("status"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Problema con la petición de estado", Toast.LENGTH_LONG).show();
                    }
                },
                context
        );
        queue.add(request);

    }
}