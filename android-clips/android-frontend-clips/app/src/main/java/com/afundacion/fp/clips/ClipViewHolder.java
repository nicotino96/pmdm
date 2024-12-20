package com.afundacion.fp.clips;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClipViewHolder extends RecyclerView.ViewHolder {
    private Clip clip;
    private TextView cellTitle;
    public ClipViewHolder(@NonNull View itemView) {
        super(itemView);
        cellTitle = itemView.findViewById(R.id.textView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clipId = clip.getId();
                Context context = view.getContext();
                Toast.makeText(context, "Clicked on ViewHolder with clipId: " + clipId, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra(VideoActivity.INTENT_CLIP_ID, clipId);
                intent.putExtra(VideoActivity.INTENT_CLIP_URL,clip.getUrlVideo());
                context.startActivity(intent);
            }
        });
    }
    public void showClip(Clip clip) {
        this.cellTitle.setText(clip.getTitle());
        this.clip = clip;
    }


}
