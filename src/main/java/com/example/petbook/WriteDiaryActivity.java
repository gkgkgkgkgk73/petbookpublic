package com.example.petbook;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteDiaryActivity extends AppCompatActivity {
    EditText et_content;
    EditText et_title;
    private Button saveButton;
    String userID;
    private DatabaseReference mDatabase;
    private static final int PICK_FROM_ALBUM=1;
    private static final int PICK_FROM_CAMERA=0;
    private static final int CROP_FROM_IMAGE = 2;
    private ImageButton uploadPictureButton;
    Uri mImageUri;

    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writediary);

        Intent fromFragment1Intent = getIntent();
        userID = fromFragment1Intent.getStringExtra("userID");
        SharedPreferences sp = sp = this.getSharedPreferences("sp", Context.MODE_PRIVATE);
        userName = sp.getString("userName","");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 뒤로가기 버튼 만들기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setBackgroundColor(Color.WHITE);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        EditText et_content = (EditText) findViewById(R.id.diary_content);
//        et_content.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); --> 자동완성 기능 끄려고..

        // 글자수 세기 만들기
        TextView tv_letterNumber = (TextView) findViewById(R.id.letter_number);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = et_content.getText().toString();
                tv_letterNumber.setText(input.length()+ " / 100");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // 현재 날짜를 구한 뒤 데이터 베이스에서 해당 날짜의 데이터가 있는지 조회
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String diary_date = simpleDateFormat.format(date);

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("user-posts").child(userID).child(diary_date).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue(Post.class) != null) {
//                    isDiary = true;
//                }
//                else {
//                    isDiary = false;
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
//            }
//        });

        saveButton = findViewById(R.id.btn_write_save);
        uploadPictureButton = findViewById(R.id.user_image);
        uploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_content = (EditText) findViewById(R.id.diary_content);
                String diary_content = et_content.getText().toString();

                ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(mDatabase);
                readAndWriteSnippets.writeNewPost(userID, diary_date, diary_content, userName);
                StorageActivity storageActivity = new StorageActivity(userID,mImageUri,diary_date);
                SharedPreferences sp = getSharedPreferences("sp",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isDiary",true);
                finish();
            }
        });
    }

    public void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_"+ String.valueOf(System.currentTimeMillis())+".jpg";
        mImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode!=RESULT_OK)
            return;

        switch (requestCode){
            case PICK_FROM_ALBUM:

                mImageUri=data.getData();
                uploadPictureButton.setImageURI(mImageUri);
                Log.d("PICK_FROM_ALBUM", mImageUri.getPath().toString());
                break;

            case PICK_FROM_CAMERA:

                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

}
