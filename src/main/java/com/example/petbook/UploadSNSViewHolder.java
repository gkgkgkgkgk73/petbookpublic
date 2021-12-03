package com.example.petbook;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class UploadSNSViewHolder extends RecyclerView.ViewHolder{
    ImageView profile_ImageView;
    TextView user_Name;
    ImageView content_ImageView;
    TextView content_TextView;
    TextView date;
    ImageView favorite_ImageView;
    ImageView comment_ImageButton;
    TextView favoriteCounter_TextView;
    String userID;
    String likesUserID;
    TextView favorite;
    TextView favorite_m;
    public UploadSNSViewHolder(Context context, View itemView) {
        super(itemView);

        SharedPreferences sp = context.getSharedPreferences("sp",Context.MODE_PRIVATE);
        likesUserID = sp.getString("userID","");
        date = itemView.findViewById(R.id.detailviewitem_date);
        profile_ImageView = itemView.findViewById(R.id.detailviewitem_profile_image);
        user_Name = itemView.findViewById(R.id.detailviewitem_profile_textview);
        content_ImageView = itemView.findViewById(R.id.detailviewitem_imageview_content);
        content_TextView = itemView.findViewById(R.id.detailviewitem_textview_content);
        favorite_ImageView = itemView.findViewById(R.id.detailviewitem_favorite_imageview);
        comment_ImageButton = itemView.findViewById(R.id.detailviewitem_comment_imageview);
        favorite = itemView.findViewById(R.id.detailviewitem_favorite_text);
        favoriteCounter_TextView = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
        favorite_m = itemView.findViewById(R.id.detailviewtiem_myoung);
        favorite_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(firebaseDatabase);
                readAndWriteSnippets.updateLike(userID,date.getText().toString(),likesUserID,1);
                String favorite_cnt = (Integer.parseInt(favoriteCounter_TextView.getText().toString())+1)+"";
                favoriteCounter_TextView.setText(favorite_cnt);
            }
        });

        comment_ImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ShowComment.class);
                v.getContext().startActivity(intent);
            }
        });

    }

}