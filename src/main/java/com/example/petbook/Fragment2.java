package com.example.petbook;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment2 extends Fragment {

    ImageView imageView;
    TextView content;
    TextView tv_date;
    Button btn_next;
    Button btn_last;
    Button btn_calendar;

    String userID;
    String instant_content;
    String add_string;
    DatabaseReference mDatabase;

    ArrayList<String> userDiaryDates;
    ArrayList<String> userDiaryContents;
    int childrenCount;
    int index_number; //?????? ????????? userDiaryDates??? ???????????? ?????????
    ViewGroup rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = (ViewGroup) inflater.inflate(R.layout.fragment_2, container, false);

        // view ??????
        content = (TextView) rootview.findViewById(R.id.frag2_content);
        imageView = (ImageView) rootview.findViewById(R.id.frag2_image);
        tv_date = (TextView) rootview.findViewById(R.id.frag2_date);
        btn_last = (Button) rootview.findViewById(R.id.btn_lastDay);
        btn_next = (Button) rootview.findViewById(R.id.btn_nextDay);
        btn_calendar = (Button) rootview.findViewById(R.id.frag2_calendar);

        btn_next.setVisibility(View.INVISIBLE);
        // ??????, ?????? ?????? ????????? ????????? ?????????
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int Width = size.x;
        int Height = size.y;

        int buttonWidth = Width / 5;
        int buttonHeight = Height / 13;
//        btn_next.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, buttonHeight));
//        btn_last.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, buttonHeight));
//        tv_date.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth * 3, buttonHeight));

        // makeTabActivity?????? userID??? Bundle??? ?????????
        Bundle bundle = getArguments();
        userID = bundle.getString("userID");


        // ?????? ????????? "yyyy-MM-dd" ????????? ???????????? firebase?????? ????????? ??????
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String diary_date = simpleDateFormat.format(date);

        // ArrayList<String> ????????? ????????????
        userDiaryContents = new ArrayList<>();
        userDiaryDates = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-posts").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childrenCount = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userDiaryDates.add(snapshot.getKey());
                    String diary_content_down = snapshot.getValue().toString();
                    String[] parse_diary = diary_content_down.split(",");
                    diary_content_down = parse_diary[3].substring(9);
                    userDiaryContents.add(diary_content_down);
                    Log.w(TAG, diary_content_down);
                    }

                // ?????? ????????? ????????? ????????? ?????????.

                if(userDiaryDates.contains(diary_date)){
                    index_number = userDiaryDates.indexOf(diary_date);
                }
                else {
                    if(userDiaryDates.size() == 0) {
                        index_number = 0;
                    }
                    else {
                        index_number = userDiaryDates.size() - 1 ; //????????? ????????? ????????? ???.
                    }
                }

                tv_date.setText(changeDate(userDiaryDates.get(index_number)) + " " + getWhatDay(userDiaryDates.get(index_number)));
                content.setText(userDiaryContents.get(index_number));
                loadImage(userDiaryContents.get(index_number));

                btn_last.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index_number == 0) {

                        }
                        else if(index_number == 1){
                            btn_last.setVisibility(View.INVISIBLE);
                            btn_next.setVisibility(View.VISIBLE);
                            index_number -= 1;
                            String last_content = userDiaryContents.get(index_number);
                            String last_date = userDiaryDates.get(index_number);

                            tv_date.setText(changeDate(last_date) + " " + getWhatDay(last_date));
                            content.setText(last_content);
                        }
                        else {
                            btn_next.setVisibility(View.VISIBLE);
                            index_number -= 1;
                            String last_content = userDiaryContents.get(index_number);
                            String last_date = userDiaryDates.get(index_number);

                            tv_date.setText(changeDate(last_date) + " " + getWhatDay(last_date));
                            content.setText(last_content);
                        }
                        loadImage(userDiaryDates.get(index_number));
                    }
                });

                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index_number == userDiaryDates.size() - 1) {

                        }
                        else if(index_number == userDiaryDates.size() - 2) {
                            btn_last.setVisibility(View.VISIBLE);
                            btn_next.setVisibility(View.INVISIBLE);
                            index_number += 1;
                            String next_content = userDiaryContents.get(index_number);
                            String next_date = userDiaryDates.get(index_number);

                            tv_date.setText(changeDate(next_date) + " " + getWhatDay(next_date));
                            content.setText(next_content);
                        }
                        else {
                            btn_last.setVisibility(View.VISIBLE);
                            index_number += 1;
                            String next_content = userDiaryContents.get(index_number);
                            String next_date = userDiaryDates.get(index_number);

                            tv_date.setText(changeDate(next_date) + " " + getWhatDay(next_date));
                            content.setText(next_content);
                        }
                        loadImage(userDiaryDates.get(index_number));
                    }

                });

//                // content??? ????????? 400?????? ??? ?????? ??? ????????? ????????????.
//                int content_length = content.length();
//                add_string = "";
//                for (int i = 0; i < 400 - content_length; i++) {
//                    add_string += " ";
//                }
//
//                instant_content = content.getText().toString();
//                instant_content += add_string;
//                Log.w(TAG, "content is: " + instant_content);
//
//                // content ?????? ??????
//                SpannableString spannableString = new SpannableString(content.getText());
//                spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
//                content.setText(spannableString);
////                content.setPaintFlags(content.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowCalendarActivity.class);
                intent.putExtra("userID", userID);
                startActivityForResult(intent, 408);
            }
        });
        return rootview;


    }

//    public void makeUnderLine(){
//        // content??? ????????? 400?????? ??? ?????? ??? ????????? ????????????.
//        int content_length = content.length();
//        add_string = "";
//        for (int i = 0; i < 400 - content_length; i++) {
//            add_string += " ";
//        }
//
//        instant_content = content.getText().toString();
//        instant_content += add_string;
//        Log.w(TAG, "content is: " + instant_content);
//
//        // content ?????? ??????
//        SpannableString spannableString = new SpannableString(content.getText());
//        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
//        content.setText(spannableString);
////                content.setPaintFlags(content.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode){
                case 408:
                    String select_date = data.getStringExtra("select_date");

                    index_number = userDiaryDates.indexOf(select_date);
                    tv_date.setText(changeDate(userDiaryDates.get(index_number)) + " " + getWhatDay(userDiaryDates.get(index_number)));
                    content.setText(userDiaryContents.get(index_number));

                    btn_next.setVisibility(View.VISIBLE);
                    btn_last.setVisibility(View.VISIBLE);

                    if(index_number == userDiaryDates.size() - 1) {
                        btn_next.setVisibility(View.INVISIBLE);
                    }
                    else if(index_number == 0) {
                        btn_last.setVisibility(View.INVISIBLE);
                    }
            }
        }
        else {

        }
    }

    public void loadImage(String date) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" + userID + "/"+date);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {
                Log.d("",String.valueOf(uri));
                Glide.with(rootview).load(uri).override(200,200).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public String changeDate(String date) {
        String[] date_parse = date.split("-");
        String sYear = date_parse[0];
        String sMonth = date_parse[1];
        String sDayOfMonth = date_parse[2];

        if (sMonth.charAt(0) == '0') {
            sMonth = "" + sMonth.charAt(1);
        }
        if (sDayOfMonth.charAt(0) == '0') {
            sDayOfMonth = "" + sDayOfMonth.charAt(1);
        }
        String changed_date = sYear + "??? " + sMonth + "??? " + sDayOfMonth + "???";

        return changed_date;
    }

    public String getWhatDay(String dayOfWeek){
        String result_day = "";
        int dayNum;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dayNum = date.getDay();
        Log.d(TAG, dayNum + "");

        switch(dayNum) {
            case 0:
                result_day = "?????????";
                break;
            case 1:
                result_day = "?????????";
                break;
            case 2:
                result_day = "?????????";
                break;
            case 3:
                result_day = "?????????";
                break;
            case 4:
                result_day = "?????????";
                break;
            case 5:
                result_day = "?????????";
                break;
            case 6:
                result_day = "?????????";
                break;
        }
        Log.d(TAG, "?????????: " + result_day);
        return result_day;
    }
}