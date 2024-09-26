package com.afundacion.fp.library;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class OnClickHandler implements View.OnClickListener{
    private Context context;

    public OnClickHandler(Context context) {
        this.context = context;
    }
    @Override
    public void onClick(View view) {
        Toast.makeText(this.context, "¡Tostada al aire!", Toast.LENGTH_LONG).show();
    }
}
