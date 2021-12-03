package com.example.petbook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WritePetInfoActivity extends Activity {
    Button btn_saveInfo;
    ImageButton btn_uploadPicture;
    TextView tv_imageSelect;
    TextView tv_birthdaySelect;
    TextView tv_alarmTime;
    TextView tv_firstDay;
    EditText et_animalName;
    private static final int PICK_FROM_ALBUM = 1;
    Uri mImageUri;
    Uri petImgUri;
    String petBD;
    String alarmTime;
    String petName;
    String petSpecies;
    String petGender;
    EditText et_animalSpecies;

    int alarmHour = 0, alarmMinute = 0;
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            if(month==12){
                petBD = year+"/"+1+"/"+dayOfMonth;}
            else{
                petBD = year+"/"+(month+1)+"/"+dayOfMonth;
            }
        }
    };

    DatePickerDialog.OnDateSetListener myDatePicker1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel1();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writepetinfo);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rb_gender);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb_btn1){
                    petGender="Male";
                }else{
                    petGender="Female";
                }
            }
        });

        Intent intent = getIntent();
        String animal_type = intent.getStringExtra("animal");

        tv_imageSelect = (TextView) findViewById(R.id.animalSelect);
        tv_birthdaySelect = (TextView) findViewById(R.id.animal_birthday);
        tv_alarmTime = (TextView) findViewById(R.id.animal_alarmTime);
        tv_firstDay = (TextView) findViewById(R.id.animal_firstDay);
        et_animalName = (EditText) findViewById(R.id.animal_name);
        et_animalSpecies = (EditText) findViewById(R.id.animal_variety);


        btn_uploadPicture = findViewById(R.id.pet_image);
        btn_uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();
            }
        });

        et_animalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                petName = et_animalName.getText().toString();
            }
        });

        et_animalSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                petSpecies = et_animalSpecies.getText().toString();
            }
        });

        btn_saveInfo = (Button) findViewById(R.id.saveInfo);
        btn_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장 구현
                //....
                if(petBD!=null && petGender !=null && petName!= null && petImgUri!= null && petSpecies!=null && alarmTime!=null) {
                    Intent nextIntent = new Intent(getApplicationContext(), MakeTabActivity.class);
                    startActivity(nextIntent);
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(dbRef);
                    Map<String, Object> petInfo = new HashMap<>();
                    petInfo.put("petName", petName);
                    petInfo.put("petSpecies",petSpecies);
                    petInfo.put("petGender",petGender);
                    petInfo.put("petBD",petBD);
                    petInfo.put("alarmTime", alarmTime);

                    SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
                    String userID = sp.getString("userID", "");

                    SharedPreferences.Editor editor = sp.edit();
                    //이미지뷰에 넣기
                    editor.putString("petName",petName);
                    editor.putString("petSpecies",petSpecies);
                    editor.putString("petGender",petGender);
                    editor.putString("petBD",petBD);
                    editor.putString("alarmTime",alarmTime);
                    editor.putString("petImgUri",petImgUri+"");
                    editor.commit();
                    readAndWriteSnippets.writePetInfo(userID, petInfo);
                    StorageActivity storageActivity = new StorageActivity(userID, petImgUri, "petImg");
                }else {

                }

                Intent nextIntent = new Intent(getApplicationContext(), MakeTabActivity.class);
                startActivity(nextIntent);

            }
        });

        tv_birthdaySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(WritePetInfoActivity.this, R.style.DialogTheme, myDatePicker,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tv_firstDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(WritePetInfoActivity.this, R.style.DialogTheme, myDatePicker1,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tv_alarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (WritePetInfoActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tv_alarmTime.setText("다이어리 작성 알림 시간: " + hourOfDay + "시 " + minute + "분");
                                alarmTime = hourOfDay+":"+minute;
                            }
                }, alarmHour, alarmMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private void updateLabel() {
        String myformat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.KOREA);

        TextView tv_birth = (TextView) findViewById(R.id.animal_birthday);
        tv_birth.setText("생일: "+ sdf.format(myCalendar.getTime()));
    }

    private void updateLabel1() {
        String myformat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.KOREA);

        TextView tv_firstDay = (TextView) findViewById(R.id.animal_firstDay);
        tv_firstDay.setText("처음 만난 날: "+ sdf.format(myCalendar.getTime()));
    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_OK)
            return;

        switch(requestCode){
            case PICK_FROM_ALBUM:
                mImageUri = data.getData();
                petImgUri=mImageUri;
                Glide.with(WritePetInfoActivity.this).load(petImgUri).apply(new RequestOptions().circleCrop()).into(btn_uploadPicture);
                tv_imageSelect.setVisibility(View.INVISIBLE);
                break;

        }
    }
}
