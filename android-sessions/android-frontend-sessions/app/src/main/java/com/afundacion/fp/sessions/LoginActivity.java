package com.afundacion.fp.sessions;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private Context context=this;
    private TextView textViewUserStatus;
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
                Intent myIntent = new Intent(context, RegisterActivity.class);
                context.startActivity(myIntent);
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

                            throw new RuntimeException(e);
                        }

                        Toast.makeText(context, "Token: " + receivedToken, Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(context, StatusActivity.class);
                        startActivity(myIntent);
                        SharedPreferences preferences = context.getSharedPreferences("SESSIONS_APP_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("VALID_USERNAME",editTextUser.getText().toString());
                        editor.putString("VALID_TOKEN", receivedToken);
                        editor.commit();
                        finish();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();
                        }
                        else{
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "El servidor respondió con "+serverCode, Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
        this.requestQueue.add(request);


    }


}
