package com.example.x3033067lastkadai;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.ImageView;

package biz.office_matsunaga.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechRecognizerSampleActivit extends Activity {
    Button button1;
    TextView textView1;
    SpeechRecognizer sr;
    //とりあえずのサンプル

    class SpeechListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "エラー " + error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "認識開始", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // TODO Auto-generated method stub
            String recognizedList = "";
            ArrayList<String> candidates = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(String recognized : candidates) {
                Log.d("onResults", recognized);
                recognizedList += recognized + "\n";
            }
            textView1.setText(recognizedList);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub

        }

    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button1 = (Button)findViewById(R.id.button1);
        textView1 = (TextView)findViewById(R.id.textView1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                sr.setRecognitionListener(new SpeechListener());
                licationContext()));
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                sr.startListening(intent);
            }

        });
    }
}

