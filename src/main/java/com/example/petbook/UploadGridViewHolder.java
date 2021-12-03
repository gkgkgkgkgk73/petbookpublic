package com.example.petbook;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class UploadGridViewHolder extends RecyclerView.ViewHolder{
        ImageView grid_image;

public UploadGridViewHolder(Context context, View itemView) {
        super(itemView);
        grid_image = itemView.findViewById(R.id.grid_image);
}

}