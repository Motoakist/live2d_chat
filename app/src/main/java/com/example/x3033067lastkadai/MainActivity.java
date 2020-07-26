
/**
 * Copyright(c) Live2D Inc. All rights reserved.
 * <p>
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at https://www.live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.example.x3033067lastkadai;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends Activity {
    private static final int MILLIS_IN_FUTURE = 11 * 500;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private Intent intent;
    private SpeechRecognizer mRecognizer;
    private TextView textView;

    private int count = 5;
    private TextView countTextView;
    private CountDownTimer countDownTimer;

    private GLSurfaceView _glSurfaceView;
    private GLRenderer _glRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JniBridgeJava.SetActivityInstance(this);
        JniBridgeJava.SetContext(this);
        _glSurfaceView = new GLSurfaceView(this);
        _glSurfaceView.setEGLContextClientVersion(2);
        _glRenderer = new GLRenderer();
        _glSurfaceView.setRenderer(_glRenderer);
        _glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(_glSurfaceView);
//        setContentView(R.layout.activity_main);
        View view = this.getLayoutInflater().inflate(R.layout.activity_main, null);
        addContentView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                        ? View.SYSTEM_UI_FLAG_LOW_PROFILE
                        : View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        );




        //ここから音声認識
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString());

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);

        // 音声認識スタートボタン
        ImageButton button = (ImageButton) findViewById(R.id.buttonView);
        //ここが原因で落ちる...
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

    @Override
    protected void onStart() {
        super.onStart();
        JniBridgeJava.nativeOnStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _glSurfaceView.onPause();
        JniBridgeJava.nativeOnPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        JniBridgeJava.nativeOnStop();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        JniBridgeJava.nativeOnDestroy();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                JniBridgeJava.nativeOnTouchesBegan(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                JniBridgeJava.nativeOnTouchesEnded(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                JniBridgeJava.nativeOnTouchesMoved(pointX, pointY);
                break;
        }
        return super.onTouchEvent(event);
    }



    //言語処理について
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d("log:: ", "準備できてます");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("log:: ", "始め！");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d("log:: ", "音声が変わった");
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d("log:: ", "新しい音声");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("log:: ", "終わりました");
        }

        @Override
        public void onError(int i) {
            switch (i) {
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    resetText();
                    textView.setText("ネットワークタイムエラー");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    resetText();
                    textView.setText("その外ネットワークエラー");
                    break;
                case SpeechRecognizer.ERROR_AUDIO:
                    resetText();
                    textView.setText("Audio エラー");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    resetText();
                    textView.setText("サーバーエラー");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    resetText();
                    textView.setText("クライアントエラー");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    resetText();
                    textView.setText("何も聞こえてないエラー");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    resetText();
                    textView.setText("適当な結果を見つけてませんエラー");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    resetText();
                    textView.setText("RecognitionServiceが忙しいエラー");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    resetText();
                    textView.setText("RECORD AUDIOがないエラー");
                    break;
            }
        }

        @Override
        public void onResults(Bundle bundle) {
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
            if (TextUtils.equals(result[0], "おはようございます")) {
                Toast.makeText(MainActivity.this, "おはよう！！", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            if (TextUtils.getTrimmedLength(result[0])>15) {
                Toast.makeText(MainActivity.this, "すごくわかりやすい！！", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            if (TextUtils.equals(result[0], "名前は")) {
                Toast.makeText(MainActivity.this, "ひより15歳、彼氏絶賛大募集中です!!", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    private void resetText() {
        countDownTimer.cancel();
        countTextView.setText("やり直し！");
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
            Log.e("EXCEPTION LOG ", "message:: " + e.getMessage());
        }
        countDownTimer = null;
    }
}
