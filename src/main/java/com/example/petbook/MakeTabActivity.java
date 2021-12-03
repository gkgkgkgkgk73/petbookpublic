package com.example.petbook;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeTabActivity extends AppCompatActivity {
    Fragment fragment1, fragment2, fragment3, fragment4, fragment5;
    String userID;
    boolean isDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maketab);

        //Intent fromMakeTabActivityIntent = getIntent();
        //String userID = fromMakeTabActivityIntent.getStringExtra("userID");

        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
        userID = sp.getString("userID", "");
        //isDiary = sp.getBoolean("isDiary", false);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragment5 = new Fragment5();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
//        bundle.putBoolean("isDiary", isDiary);
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
        // 초기화면 설정


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item){
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                                return true;

                            case R.id.tab2:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                                return true;

                            case R.id.tab3:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                                return true;

                            case R.id.tab4:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment4).commit();
                                return true;

                            case R.id.tab5:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment5).commit();
                                return true;
                        }
                        return false;
                    }
                }
        );

    }

}
