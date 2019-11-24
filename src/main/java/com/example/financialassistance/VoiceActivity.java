package com.example.financialassistance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

//import io.kommunicate.KmConversationBuilder;
//import io.kommunicate.Kommunicate;
//import io.kommunicate.callbacks.KmCallback;
//import io.kommunicate.users.KMUser;

public class VoiceActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private String text;
    private TextToSpeech textToSpeech;
    private int result;
    private String blueResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);


        speak();


//        Kommunicate.init(this, getApplication().getString(R.string.app_id));

        /* all voice activity occurs here */

        Button logoutClick = (Button) findViewById(R.id.logoutButton);

        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override

            /* On click, initiate dialogue flow */

            public void onClick(View v) {


                Intent intent = new Intent(VoiceActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        /* run dialogue flow constantly until next click */


        Button endVoiceClick = (Button) findViewById(R.id.endVoiceButton);

        endVoiceClick.setOnClickListener(new View.OnClickListener() {
            @Override

            /* On click, return to last activity */

            public void onClick(View v) {

                Intent intent = new Intent(VoiceActivity.this, PushToTalkActivity.class);
                startActivity(intent);



            }
        });

    }

    private void speak(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Welcome");

        try {

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);


        }

        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /* method to convert blue's response text to speech */

    private void toSpeech(){

        textToSpeech = new TextToSpeech(VoiceActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status){
                if (status == TextToSpeech.SUCCESS){
                    result = textToSpeech.setLanguage(Locale.UK);

                    textToSpeech.setPitch(0.6f);
                    textToSpeech.setSpeechRate(1.0f);
                    blueSpeaking(blueResponse);


                }

            }
        }) ;


    }

    /* helper method for toSpeech() */

    private void blueSpeaking (String text) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);




    }

    /* method to ensure text to speech is terminated */

    @Override
    protected void onDestroy() {

        if (textToSpeech != null){

            textToSpeech.stop();
            textToSpeech.shutdown();

        }

        super.onDestroy();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    text = result.get(0);

                    /* dialogue method needs to accept text and write to blueResponse */

                    try {
                        NetworkManager.sharedInstance.detectIntent(text, new NetworkResponseCallback() {
                            @Override
                            public void success(JSONObject json) {
                                try {
                                    Toast.makeText(getApplicationContext(), json.getJSONObject("queryResult").getString("fulfillmentText"), Toast.LENGTH_LONG).show();
                                    blueResponse = json.getJSONObject("queryResult").getString("fulfillmentText");
                                    toSpeech();
                                } catch (JSONException jsonE) {
                                    Log.d("VocieActivity", jsonE.getLocalizedMessage());
                                }
//                                speak();
                            }
                            @Override
                            public void failure() {

                            }

                        });
                    } catch (JSONException e) {
                        Log.d("VoiceActivity", e.getLocalizedMessage());
                    }


                    /* toSpeech is called, using the variable that was just written to */

                    toSpeech();

                }
                break;
        }
    }




}
