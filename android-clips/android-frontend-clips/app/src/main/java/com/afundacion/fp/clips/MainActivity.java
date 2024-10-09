package com.afundacion.fp.clips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private ClipsList clips;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.queue = Volley.newRequestQueue(context);
        this.mainLayout = findViewById(R.id.main_layout); // Lo asignamos con findViewById
        this.progressBar = findViewById(R.id.myProgressBar);
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
    }
    private void getClips() {
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Server.name + "/clips",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(mainLayout, "List obtained", Snackbar.LENGTH_LONG).show();
                        setClips(new ClipsList(response));
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressBar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse == null) {
                            Snackbar.make(mainLayout, "Problems with the connection!", Snackbar.LENGTH_LONG).show();
                        } else {
                            int serverCode = error.networkResponse.statusCode;
                            Snackbar.make(mainLayout, "Server responded with "+serverCode, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
        );
        this.queue.add(jsonArrayRequest);

    }
    public void setClips(ClipsList clips) {
        this.clips = clips;
    }
    public ClipsList getClipsForTest() {
        return clips;
    }



}

