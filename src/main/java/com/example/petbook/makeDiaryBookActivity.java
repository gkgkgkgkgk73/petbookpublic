package com.example.petbook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class makeDiaryBookActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.make_diarybook);

        ImageView diary_sample = findViewById(R.id.ex_diary_image);
        ImageView diary_color_sample = findViewById(R.id.ex_diary_color);
        TextView diary_explanation = findViewById(R.id.diary_explanation);
        Button payment= findViewById(R.id.payment);
        payment.setVisibility(View.VISIBLE);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(makeDiaryBookActivity.this,"결제하셨습니다.",Toast.LENGTH_SHORT);
                SharedPreferences sp = makeDiaryBookActivity.this.getSharedPreferences("sp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("payment",true);
                editor.commit();
                finish();
            }
        });

    }
}
