package com.example.petbook;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class UploadSNSViewAdapter extends RecyclerView.Adapter<UploadSNSViewHolder> {

    View view;
    private ArrayList<Post> datas = new ArrayList<>();
    AdapterView.OnItemClickListener listener;

    public UploadSNSViewAdapter(ArrayList<Post> data){
        this.datas = data;
    }
    @NonNull
    @Override
    public UploadSNSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_detail,parent,false);
        UploadSNSViewHolder viewHolder = new UploadSNSViewHolder(context, view);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull UploadSNSViewHolder holder, int position) {
        Post uploadLinearDiary = datas.get(position);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" + uploadLinearDiary.userID + "/"+uploadLinearDiary.date);
        StorageReference profileImg = storageRef.child("images/" + uploadLinearDiary.userID +"/petImg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Glide.with(view).load(uri).override(200,200).into(holder.content_ImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        profileImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Glide.with(view).load(uri).override(35,35).circleCrop().into(holder.profile_ImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.userID = uploadLinearDiary.userID;
        holder.user_Name.setText(uploadLinearDiary.userName);
        holder.date.setText(uploadLinearDiary.date);
        holder.content_TextView.setText(uploadLinearDiary.content);
        holder.favoriteCounter_TextView.setText(uploadLinearDiary.likes+"");
    }

    @Override
    public int getItemCount() {
        Log.d("itemCount",datas.size()+"");
        return datas.size();
    }


    public void setDatas(Post data){
        datas.add(data);
    }

}
