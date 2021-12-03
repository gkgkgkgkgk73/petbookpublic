package com.example.petbook;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UploadGridAdapter extends RecyclerView.Adapter<UploadGridViewHolder> {

    View view;
    private ArrayList<Post> datas = new ArrayList<>();


    public UploadGridAdapter(ArrayList<Post> data){
        this.datas = data;
    }
    @NonNull
    @Override
    public UploadGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.grid_item,parent,false);
        UploadGridViewHolder viewHolder = new UploadGridViewHolder(context, view);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull UploadGridViewHolder holder, int position) {
        Post uploadLinearDiary = datas.get(position);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" + uploadLinearDiary.userID + "/" + uploadLinearDiary.date);

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Glide.with(view).load(uri).override(200,200).into(holder.grid_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        //TODO userName은 null로 나오는 현상 고치기
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