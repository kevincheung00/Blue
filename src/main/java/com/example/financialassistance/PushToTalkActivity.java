package com.example.financialassistance;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PushToTalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Button logoutClick = (Button) findViewById(R.id.logoutButton);

        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override

            /* On click, initiate dialogue flow */

            public void onClick(View v) {


                Intent intent = new Intent(PushToTalkActivity.this, MainActivity.class);
                startActivity(intent);



            }
        });

        Button voiceClick = (Button) findViewById(R.id.voiceButton);

        voiceClick.setOnClickListener(new View.OnClickListener() {
            @Override

            /* On click, initiate dialogue flow */

            public void onClick(View v) {


                Intent intent = new Intent(PushToTalkActivity.this, VoiceActivity.class);
                startActivity(intent);




            }
        });

    }



}
