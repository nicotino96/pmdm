package com.afundacion.fp.clips;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ClipsAdapter extends RecyclerView.Adapter<ClipViewHolder> {
    private ClipsList clipsToBePresented;

    public ClipsAdapter(ClipsList clips) {
        this.clipsToBePresented = clips;
    }

    @NonNull
    @Override
    public ClipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Necesitamos un LayoutInflater. Lo creamos a partir de un Context
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 2. Con el LayoutInflater, 'inflamos' el XML y generamos una View
        View cellView = inflater.inflate(R.layout.recycler_view_cell, parent, false);
        // 3. Esta View es la que pasamos al constructor de ClipViewHolder.
        //    ¡Y ya está listo!
        ClipViewHolder cellViewHolder = new ClipViewHolder(cellView);
        return cellViewHolder;
    }


        @Override
        public void onBindViewHolder(@NonNull ClipViewHolder holder, int position) {
            // Usamos .get(position) para acceder al 'enésimo' elemento de la lista
            // O sea, el correspondiente a la posición 'position'
            Clip dataForThisCell = this.clipsToBePresented.getClips().get(position);
            holder.showClip(dataForThisCell);
        }


    @Override
    public int getItemCount() {
        return this.clipsToBePresented.getClips().size();
    }
}

