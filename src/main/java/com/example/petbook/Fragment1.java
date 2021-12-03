package com.example.petbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment1 extends Fragment {

    TextView tv_date;
    String userID;
    static boolean isDiary;
    private DatabaseReference mDatabase;
    TextView btn_moveToWrite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_1, container, false);
        btn_moveToWrite = rootview.findViewById(R.id.moveToWrite);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        SharedPreferences sp = getActivity().getSharedPreferences("sp",Context.MODE_PRIVATE);
        isDiary = sp.getBoolean("isDiary",false);

        tv_date = (TextView) rootview.findViewById(R.id.fragment_1_date);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일");
        String time = simpleDateFormat.format(date);
        tv_date.setText(time);

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("YYYY-MM-dd");
        String diary_date = simpleDateFormat2.format(date);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        try {
//            mDatabase.child("user-posts").child(userID).child(diary_date).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post != null) {
//                        if(dataSnapshot.getValue(Post.class).content.length() > 0) {
//                            Log.w("FireBaseData", "getData " + dataSnapshot.getValue(Post.class).content);
//                            isDiary = true;
//                        }
//                        else {
//                            isDiary = false;
//                        }
//                    }
//                    else {
//                        isDiary = false;
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
//                }
//            });
//        }
//        catch(Exception e) {
//
//        }

        if (isDiary) {
            btn_moveToWrite.setText("오늘의 일기를 쓰셨네요!");
        }else{
            btn_moveToWrite.setText("오늘의 일기 쓰러가기");
        }

        btn_moveToWrite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isDiary) {
                    Toast.makeText(getActivity(), "해당 날짜의 일기가 있습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), WriteDiaryActivity.class);
                    intent.putExtra("userID",userID);
                    startActivity(intent);
                }
            }
        });

        return rootview;
        // return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();

//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
//        String time = simpleDateFormat.format(date);
//        String[] time_parse = time.split("-");
//        String sMonth = time_parse[0];
//        String sDayOfMonth = time_parse[1];
//        if (sMonth.charAt(0) == '0'){
//            sMonth = "" + sMonth.charAt(1);
//        }
//        if (sDayOfMonth.charAt(0) == '0'){
//            sDayOfMonth = "" + sDayOfMonth.charAt(1);
//        }
//        String new_time = sMonth + "월 " + sDayOfMonth + "일";
//
//        tv_date.setText(new_time);
//
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//        String diary_date = simpleDateFormat2.format(date);
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        try {
//            mDatabase.child("user-posts").child(userID).child(diary_date).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post != null) {
//                        if (dataSnapshot.getValue(Post.class).content.length() > 0) {
//                            isDiary = true;
//                        } else {
//                            isDiary = false;
//                        }
//                    } else {
//                        isDiary = false;
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
//                }
//            });
//        } catch (Exception e) {
//
//        }
    }

    @Override
    public void onResume() {

        if (isDiary) {
            btn_moveToWrite.setText("오늘의 일기를 쓰셨네요!");
        }else{
            btn_moveToWrite.setText("오늘의 일기 쓰러가기");
        }


        super.onResume();
    }

}
