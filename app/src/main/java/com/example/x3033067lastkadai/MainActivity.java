package com.example.x3033067lastkadai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "Sample";
    private SpeechRecognizer mRecognizer;

    private TextView textView;

    private int count = 5;
    private TextView countTextView;

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onError(int error) {
            if ((error == SpeechRecognizer.ERROR_NO_MATCH) ||
                    (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
                startSpeechRecognition();
                return;
            }
            Log.d(TAG, "Recognition Error: " + error);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> values = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String val = values.get(0);
            Log.d(TAG, "認識結果: " + val);
            startSpeechRecognition();
        }

        @Override public void onBeginningOfSpeech() {}
        @Override public void onBufferReceived(byte[] arg0) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onEvent(int arg0, Bundle arg1) {}
        @Override public void onPartialResults(Bundle arg0) {}
        @Override public void onReadyForSpeech(Bundle arg0) {}
        @Override public void onRmsChanged(float arg0) {}
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        countTextView = (TextView) findViewById(R.id.countTextViwe);

        // permission チェック
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO)) {
                // 拒否した場合
            } else {
                // 許可した場合
                int MY_PERMISSIONS_RECORD_AUDIO = 1;
                ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }




    }




    private void startSpeechRecognition() {
        // Need to destroy a recognizer to consecutive recognition?
        if (mRecognizer != null) {
            mRecognizer.destroy();
        }

        // Create a recognizer.
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(mRecognitionListener);

        // Start recognition.
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizer.startListening(intent);
    }

    SpeechRecognizerSampleActivity srsa = new SpeechRecognizerSampleActivity;
    srsa.startSpeechRecognition;

}