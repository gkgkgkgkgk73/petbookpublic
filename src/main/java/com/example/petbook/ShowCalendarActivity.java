package com.example.petbook;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class ShowCalendarActivity extends Activity {

    String userID;
    DatabaseReference mDatabase;

    int childrenCount;
    ArrayList<String> userDiaryDates;
    ArrayList<String> userDiaryContents;
    HashSet<CalendarDay> userCalendarDays;

    static Toast sToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcalendar);

        Intent fromFragment2Intent = getIntent();
        userID = fromFragment2Intent.getStringExtra("userID");

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        userDiaryDates = new ArrayList<>();
        userDiaryContents = new ArrayList<>();

        //-------------------------------------------------------------
        // showCalendarActivity에서 전체 diary_date와 diary_content를 받아온다..
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-posts").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                childrenCount = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userDiaryDates.add(snapshot.getKey());
                    Log.w(TAG, "isisis" + snapshot.getKey());
                    String diary_content_down = snapshot.getValue().toString();
                    String[] parse_diary = diary_content_down.split(",");
                    int diary_content_length = diary_content_down.length();
                    diary_content_down = parse_diary[3].substring(9);

                    //Log.w(TAG, "diary_content? : " + diary_content_down);
                    userDiaryContents.add(diary_content_down);

                }

                Log.w(TAG, "look!!" + userDiaryDates.toString());

                //-------------------------------------------------------------
                // 저장된 ArrayList 형식의 Diary_date 들을 CalendarDay형식의 Hashset에 저장하자
                userCalendarDays = transDates(userDiaryDates);
                //------------------------------------------------------------
                // 캘린더에 데코레이터 추가
                EventDecorator eventDecorator = new EventDecorator(Color.RED, userCalendarDays);
                TodayDecorator todayDecorator = new TodayDecorator();

                materialCalendarView.addDecorator(eventDecorator);
                materialCalendarView.addDecorator(todayDecorator);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        materialCalendarView.setTopbarVisible(true);

        materialCalendarView.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .setFirstDayOfWeek(Calendar.SUNDAY) //일요일을 시작
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.setDateTextAppearance(R.style.customCalendarViewFontStyle);
        materialCalendarView.setWeekDayTextAppearance(R.style.customCalendarViewFontStyle);
        materialCalendarView.setHeaderTextAppearance(R.style.customCalendarViewFontStyle);



        materialCalendarView.setDynamicHeightEnabled(false);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                // 선택된 date 형식의 날짜를 "yyyy-MM-dd" 형식의 String으로 변환
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

                Date date_in_showDiary = null;
                try {
                    date_in_showDiary = sdf1.parse(date.getDate().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String select_date = sdf2.format(date_in_showDiary);
                // -----------------------------------------------------------------------

                if(userDiaryDates.contains(select_date)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("select_date", select_date);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else{
                    showToast(getApplicationContext(), "해당 일기가 없습니다.");
//                    Log.w(TAG, "showToast 작동중...");
                }
            }
        });
    }

    public static void showToast(Context context, String message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }

    // 닫기 버튼 클릭
    public void mOnClose(View v) {
        finish();
    }

    public HashSet<CalendarDay> transDates(ArrayList<String> dates) {
        Calendar calendar = Calendar.getInstance();
        HashSet<CalendarDay> result;

        result = new HashSet<>();

        for(int i = 0; i < dates.size(); i++) {
            String[] time = dates.get(i).split("-");

            int year = Integer.parseInt(time[0]);
            int month = Integer.parseInt(time[1]);
            int dayOfMonth = Integer.parseInt(time[2]);

            calendar.set(year, month - 1, dayOfMonth);
            CalendarDay day = CalendarDay.from(calendar);
            result.add(day);
        }
        return result;
    }
}
