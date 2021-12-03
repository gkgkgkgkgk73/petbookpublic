package com.example.petbook;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.json.Json;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ShowDiaryActivity extends Activity {

    TextView tv_date;
    TextView tv_content;
    File imageFile;
    Button btn_next;
    Button btn_last;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    //파이어베이스에서 이미지 가져오기
    DatabaseReference mDatabase;

    int childrenCount;
    String[] userDiaryDates;
    String[] userDiaryContents;
    ArrayList<String> userDatesArrayList;

    int index_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showdiary);

        tv_date = findViewById(R.id.showdiary_date);
        tv_content = findViewById(R.id.showdiary_content);

        btn_next = findViewById(R.id.btn_nextDay);
        btn_last = findViewById(R.id.btn_lastDay);

        // 버튼, 날짜 출력 뷰들의 크기를 정해줌
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int Width = size.x;
        int Height = size.y;

        int buttonWidth = Width / 5;
        int buttonHeight = Height / 13;
        btn_next.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, buttonHeight));
        btn_last.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, buttonHeight));
        tv_date.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth * 3, buttonHeight));

        //Fragment2 에서 선택된 날짜 데이터 받아오기 ------------------------------------
        Intent intent = getIntent();
        String diary_date = intent.getStringExtra("date");
        String userID = intent.getStringExtra("userID");
        String diary_content = intent.getStringExtra("content");
        ImageView imageView = findViewById(R.id.showdiary_image);

        //이미지뷰에 넣기
        // last_date 가 "2021-11-06" 형식인데 이것을 "2021년 11월 6일"로 바꾸자
        String[] date_parse = diary_date.split("-");
        String s1Year = date_parse[0];
        String s1Month = date_parse[1];
        String s1DayOfMonth = date_parse[2];

        if (s1Month.charAt(0) == '0'){
            s1Month = "" + s1Month.charAt(1);
        }
        if (s1DayOfMonth.charAt(0) == '0'){
            s1DayOfMonth = "" + s1DayOfMonth.charAt(1);
        }

        String changed_diary_date = s1Year + "년 " + s1Month + "월 " + s1DayOfMonth + "일";

        tv_date.setText(changed_diary_date);
        tv_content.setText(diary_content);

//        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/"+diary_date);
//
//        if(!file.isDirectory()){
//            file.mkdir();
//        }

        Log.d("userID",userID);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Diary Date 날짜들과 그 숫자들을 저장
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-posts").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                childrenCount = (int) dataSnapshot.getChildrenCount();
                userDiaryDates = new String[childrenCount];
                userDiaryContents = new String[childrenCount];

                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userDiaryDates[cnt] = snapshot.getKey();
                    //Log.w(TAG, "isisis" + snapshot.getValue().toString().getClass());
                    String diary_content_down = snapshot.getValue().toString();

                    String[] parse_diary = diary_content_down.split(",");
                    int diary_content_length = diary_content_down.length();
                    diary_content_down = parse_diary[3].substring(9);
                    Log.w(TAG, diary_content_down);
                    //Log.w(TAG, "diary_content? : " + diary_content_down);
                    userDiaryContents[cnt] = diary_content_down;
                    cnt++;
                }

                Log.w(TAG, "first Diary is : " + userDiaryContents[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //TODO userDiaryDates를 json 형태로 sp에 저장하기
//        for(int i = 0; i < userDiaryDates.length; i++) {
//            userDatesArrayList.add(userDiaryDates[i]);
//        }
//        setStringArrayPref(this, "userDiaryDates", userDatesArrayList);

        // 지금까지 dia rydate list, diarycontent list, diary 개수 구함.

//        SlidingView sv = new SlidingView(this);
//        ArrayList<View> viewArrayList = new ArrayList<View>(childrenCount);
//        for (int i = 0 ; i < childrenCount; i++) {
//            viewArrayList.add(i, View.inflate(this, R.layout.showdiary, null));
//        }
//        for (int i = 0; i < childrenCount; i++) {
//            View view = viewArrayList.get(i);
//            TextView tv_content = (TextView) view.findViewById(R.id.diary_content);
//            TextView tv_date = (TextView) view.findViewById(R.id.diary_date);
//
//            tv_content.setText(userDiaryContents[i]);
//            tv_date.setText(userDiaryDates[i]);
//        }
//
//        // sv에 addview
//        for (int i = 0; i < childrenCount; i++) {
//            sv.addView(viewArrayList.get(i));
//        }
//        setContentView(sv);


        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" + userID + "/"+diary_date);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Log.d("",String.valueOf(uri));
                Glide.with(ShowDiaryActivity.this).load(uri).override(200,200).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        for (int i = 0; i < childrenCount; i++) {
            if (userDiaryDates[i].equals(diary_date)) {
                index_num = i;
                break;
            }
        }

        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_num < 1) {
                    //Toast.makeText(getApplicationContext(), "더 없습니다", Toast.LENGTH_SHORT).show();
                    index_num = childrenCount - 1;
                } else {
                    index_num -= 1;
                }
                String last_content = userDiaryContents[index_num];
                String last_date = userDiaryDates[index_num];

                // last_date 가 "2021-11-06" 형식인데 이것을 "2021년 11월 6일"로 바꾸자
                String[] date_parse = last_date.split("-");
                String sYear = date_parse[0];
                String sMonth = date_parse[1];
                String sDayOfMonth = date_parse[2];

                if (sMonth.charAt(0) == '0'){
                    sMonth = "" + sMonth.charAt(1);
                }
                if (sDayOfMonth.charAt(0) == '0'){
                    sDayOfMonth = "" + sDayOfMonth.charAt(1);
                }

                last_date = sYear + "년 " + sMonth + "월 " + sDayOfMonth + "일";

                tv_date.setText(last_date);
                tv_content.setText(last_content);

                // 사진 받아오는 곳
//                StorageReference fileRef = storageRef.child("images/" + userID + "/"+ last_date);
//                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(@NonNull Uri uri) {
//                        Glide.with(ShowDiaryActivity.this).load(uri).override(20).into(imageView);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_num >= childrenCount - 1) {
                    //Toast.makeText(getApplicationContext(), "더 없습니다", Toast.LENGTH_SHORT).show();
                    index_num = 0;
                } else {
                    index_num += 1;
                }
                String last_content = userDiaryContents[index_num];
                String last_date = userDiaryDates[index_num];

                // last_date 가 "2021-11-06" 형식인데 이것을 "2021년 11월 6일"로 바꾸자
                String[] date_parse = last_date.split("-");
                String sYear = date_parse[0];
                String sMonth = date_parse[1];
                String sDayOfMonth = date_parse[2];

                if (sMonth.charAt(0) == '0'){
                    sMonth = "" + sMonth.charAt(1);
                }
                if (sDayOfMonth.charAt(0) == '0'){
                    sDayOfMonth = "" + sDayOfMonth.charAt(1);
                }

                last_date = sYear + "년 " + sMonth + "월 " + sDayOfMonth + "일";

                tv_date.setText(last_date);
                tv_content.setText(last_content);
            }
        });
    }


    // 닫기 버튼 클릭
    public void mOnClose(View v) {
        finish();
    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }

    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if(!values.isEmpty()) {
            editor.putString(key, a.toString());
        }
        else {
            editor.putString(key, null);
        }
        editor.apply();
    }

//    private ArrayList<String> getStringArrayPref(Context context, String key) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        String json = prefs.getString(key, null);
//        ArrayList<String> urls = new ArrayList<String>();
//        if (json != null) {
//            try {
//                JSONArray a = new JSONArray(json);
//                for (int i = 0; i < a.length(); i++) {
//                    String url = a.optString(i);
//                    urls.add(url);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return urls;
//    }
}
