package com.example.petbook;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

public class Fragment5 extends Fragment {
    SharedPreferences sp;

    // 다이어리 책 만들기를 넣었음
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_5, container, false);
        Button btn_logout = (Button) rootview.findViewById(R.id.btn_logout);
        //Button btn_delete_user = (Button) rootview.findViewById(R.id.btn_delete_user);

        Button make_diary = rootview.findViewById(R.id.make_book);

        ImageView pet_Img = rootview.findViewById(R.id.petImg);
        TextView userName = rootview.findViewById(R.id.user_name);
        TextView petName = rootview.findViewById(R.id.pet_name);
        TextView petSpecies = rootview.findViewById(R.id.pet_Species);
        TextView petBD = rootview.findViewById(R.id.pet_BD);
        TextView petGender=rootview.findViewById(R.id.pet_gender);
        TextView alarmTime = rootview.findViewById(R.id.alarmTime);

        sp = getActivity().getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //이미지뷰에 넣기


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImg = storageRef.child("images/" + sp.getString("userID","") +"/petImg");


        profileImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Glide.with(rootview).load(uri).override(100,100).circleCrop().into(pet_Img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //Glide.with(getActivity()).load(?).apply(new RequestOptions().circleCrop()).into(pet_Img);
        userName.setText("유저이름: "+sp.getString("userName",""));
        petName.setText("반려동물 이름: "+sp.getString("petName", ""));
        petSpecies.setText("반려동물 종: "+sp.getString("petSpecies",""));
        petBD.setText("반려동물 생일: "+sp.getString("petBD", ""));
        petGender.setText("반려동물 성별: "+sp.getString("petGender",""));
        alarmTime.setText("알람시간: "+sp.getString("alarmTime",""));

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getActivity().getSharedPreferences("sp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isLogin", false);
                editor.putString("userID","");
                editor.commit();

                signOut();
                Intent intent = new Intent(getActivity(), LoadingActivity.class);
                startActivity(intent);
            }
        });
        make_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),makeDiaryBookActivity.class);
                startActivity(intent);
            }
        });


        return rootview;
    }

    public void signOut() {
        // [START auth_fui_signout]
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        mAuth.signOut();
        saveLogout();
    }

    public void saveLogout(){
        SharedPreferences sp = getActivity().getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isLogin", false);
        editor.commit();
    }



}
