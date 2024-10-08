package com.afundacion.fp.clips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    private Context context = this;
    private ConstraintLayout mainLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.name + "/health",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, "Response OK: " + response.getString("status"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    // Error: No se ha establecido la conexión
                    Toast.makeText(context, "Problems with the connection!", Toast.LENGTH_LONG).show();

                } else {
                    // Error: El servidor ha dado una respuesta de error

                    // La siguiente variable contendrá el código HTTP,
                    // por ejemplo 404, 500,...
                    int serverCode = error.networkResponse.statusCode;
                    Toast.makeText(context, "Server status: " + serverCode, Toast.LENGTH_LONG).show();

                }

            }
        }

        );
        getClips();
        this.queue.add(request);
        this.mainLayout = findViewById(R.id.main_layout); // Lo asignamos con findViewById

    }
    private void getClips() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Server.name + "/clips",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Snackbar.make(mainLayout, "List obtained", Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        );
        this.queue.add(jsonArrayRequest);

    }

}

