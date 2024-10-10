package com.afundacion.fp.clips;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CharacterViewHolder extends RecyclerView.ViewHolder {
    private TextView textName;
    private ImageView characterImageView;


    public CharacterViewHolder(@NonNull View itemView) {
        super(itemView);
        textName = itemView.findViewById(R.id.text_view_character);
        characterImageView = itemView.findViewById(R.id.image_view_character);
    }

    public void showData(Character character) {
        this.textName.setText(character.getName());
        Util.downloadBitmapToImageView(character.getImageUrl(), this.characterImageView);
    }
}

