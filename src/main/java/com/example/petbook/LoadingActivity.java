package com.example.petbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadingActivity extends Activity {
    boolean isLogin;
    int delayTime = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        startLoading();
    }

    private void startLoading() {
        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
        isLogin = sp.getBoolean("isLogin", false);

        Log.w("loadingAct","login is " + isLogin);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(!isLogin){
                    intent = new Intent(getBaseContext(), MainActivity.class);
                }
                else {
                    intent = new Intent(getBaseContext(), MakeTabActivity.class);
                }
                startActivity(intent);

                //여기서 다이어리 있는지 찾아주기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
                String time = simpleDateFormat.format(date);
                String[] time_parse = time.split("-");
                String sMonth = time_parse[0];
                String sDayOfMonth = time_parse[1];
                if (sMonth.charAt(0) == '0'){
                    sMonth = "" + sMonth.charAt(1);
                }
                if (sDayOfMonth.charAt(0) == '0'){
                    sDayOfMonth = "" + sDayOfMonth.charAt(1);
                }
                String new_time = sMonth + "월 " + sDayOfMonth + "일";


                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                String diary_date = simpleDateFormat2.format(date);
                String userID = sp.getString("userID","");
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                try {
                    mDatabase.child("user-posts").child(userID).child(diary_date).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (post != null) {
                                if (dataSnapshot.getValue(Post.class).content.length() > 0) {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("isDiary",true);
                                    editor.commit();
                                } else {
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("isDiary",false);
                                    editor.commit();
                                }
                            } else {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("isDiary",false);
                                editor.commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
                        }
                    });
                } catch (Exception e) {

                }
                finish();
            }
        }, delayTime);
    }
}
