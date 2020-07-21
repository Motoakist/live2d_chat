package com.example.x3033067lastkadai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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
    private Intent intent;

    private TextView textView;

    private int count = 5;
    private CountDownTimer countDownTimer;

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
            String key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] result = new String[0];
            if (mResult != null) {
                result = new String[mResult.size()];
            }
            if (mResult != null) {
                mResult.toArray(result);
            }

            textView.setText(result[0]);

            // テキスト比較
            if (TextUtils.equals(result[0], "メリークリスマス")) {
                Toast.makeText(MainActivity.this, "あなたもね！！", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
        }

        @Override public void onBeginningOfSpeech() {}
        @Override public void onBufferReceived(byte[] bundle) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onEvent(int bundle, Bundle arg1) {}
        @Override public void onPartialResults(Bundle bundle) {}
        @Override public void onReadyForSpeech(Bundle bundle) {}
        @Override public void onRmsChanged(float bundle) {}
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

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // 音声認識スタートボタン
        Button button = (Button) findViewById(R.id.buttonView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 音声テキスト初期化
                if (!TextUtils.isEmpty(textView.getText())) {
                    textView.setText("");
                }

                // カウントダウンスタート
                countDownTimer();
                countDownTimer.start();

                // レコーディングスタート
                mRecognizer.startListening(intent);
            }
        });



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

    public void countDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            count = 5;
        }

        countDownTimer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
//                String countString = getString(R.string.count_string, count);
//                countTextView.setText(countString);
                count--;
            }

            public void onFinish() {
                countDownTimer.cancel();
                countTextView.setText(String.valueOf("やり直し？"));
            }
        };
    }
}