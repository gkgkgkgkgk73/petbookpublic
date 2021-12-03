package com.example.petbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//댓글 버튼 누르면 오는 페이지, 댓글들 보여줌
public class ShowComment extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_comment);
        Button close_Button = findViewById(R.id.close_button);
        close_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
