package com.afundacion.fp.sessions;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button regButton;
    private Button logButton;
    private EditText editTextUser;
    private EditText editTextPass;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUser = findViewById(R.id.usuario_login);
        editTextPass = findViewById(R.id.password_login);
        regButton = findViewById(R.id.boton_registro);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });
        logButton = findViewById(R.id.boton_login);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostLogin();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
    }
    private void sendPostLogin() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", editTextUser.getText().toString());
            requestBody.put("password",editTextPass.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.name + "/sessions",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String receivedToken;
                        try {
                            receivedToken = response.getString("sessionToken");
                        } catch (JSONException e) {
                            // Si el JSON de la respuesta NO contiene "sessionToken", vamos a lanzar
                            // una RuntimeException para que la aplicación rompa.
                            // En preferible que sea NOTORIO el problema del servidor, pues desde
                            // la aplicación no podemos hacer nada. Estamos 'vendidos'.
                            throw new RuntimeException(e);
                        }
                        // Si la respuesta está OK, mostramos un Toast
                        // Esta línea asume que private Context context = this; está definido
                        Toast.makeText(LoginActivity.this, "Token: " + receivedToken, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        this.requestQueue.add(request);


    }


}
