package com.example.petbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.user.UserApiClient;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SelectAnimalActivity extends Activity {
    String animal;
    TextView tv_animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectanimal);

        Button btn_toNext = (Button) findViewById(R.id.btn_toNext);
        Button btn_dog = (Button) findViewById(R.id.btn_dog);
        Button btn_cat = (Button) findViewById(R.id.btn_cat);
        Button btn_hamster = (Button) findViewById(R.id.btn_hamster);
        Button btn_bird = (Button) findViewById(R.id.btn_bird);
        Button btn_fish = (Button) findViewById(R.id.btn_fish);
        Button btn_rabbit = (Button) findViewById(R.id.btn_rabbit);
        Button btn_others = (Button) findViewById(R.id.btn_others);

        btn_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "dog";
            }
        });

        btn_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "cat";
            }
        });

        btn_hamster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "hamster";
            }
        });

        btn_bird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "bird";
            }
        });

        btn_fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "fish";
            }
        });

        btn_rabbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "rabbit";
            }
        });

        btn_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animal = "others";
            }
        });

        btn_toNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animal!=null){
                    Intent intent = new Intent(getApplicationContext(), WritePetInfoActivity.class);
                    intent.putExtra("animal", animal);
                    startActivity(intent);
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(dbRef);
                    Map<String, Object> petspecies = new HashMap<>();
                    petspecies.put("petSpecies", animal);
                    SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
                    String userID = sp.getString("userID", "");
                    readAndWriteSnippets.writePetInfo(userID, petspecies);
                    finish();
            }}
        });
    }

    @Override
    public void onResume(){
        tv_animal = (TextView) findViewById(R.id.animal);
        tv_animal.setText(animal);

        super.onResume();
    }
}
