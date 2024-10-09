package com.afundacion.fp.clips;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClipViewHolder extends RecyclerView.ViewHolder {
    private TextView cellTitle;
    public ClipViewHolder(@NonNull View itemView) {
        super(itemView);
        cellTitle = itemView.findViewById(R.id.textView);

    }
}
