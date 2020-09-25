
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
import android.content.res.AssetManager;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends Activity {
    //countDownタイマーの秒数
    private static final int MILLIS_IN_FUTURE = 8500;
    //countするインターバル1秒
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private Intent intent;
    private SpeechRecognizer mRecognizer;
    private TextView textView;

    private int count = 8;
    private TextView countTextView;
    private CountDownTimer countDownTimer;

    private GLSurfaceView _glSurfaceView;
    private GLRenderer _glRenderer;

    private int expression_counter;

    int dic_len = 55120;
    String[] dic_w = new String[dic_len];
    String[] dic_n = new String[dic_len];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        InputStream isw = null;
        BufferedReader brw = null;
        int i = 0;
        try {
            try {
                // assetsフォルダ内の sample.txt をオープンする
                isw = this.getAssets().open("words.txt");
                brw = new BufferedReader(new InputStreamReader(isw));
                // １行ずつ読み込み、改行を付加する
                String str;
                while ((str = brw.readLine()) != null) {
                    dic_w[i] = str;
                    i += 1;
                }
            } finally {
                if (isw != null) isw.close();
                if (brw != null) brw.close();
            }
        } catch (Exception e){
            // エラー発生時の処理
        }

        InputStream isn = null;
        BufferedReader brn = null;
        i = 0;
        try {
            try {
                // assetsフォルダ内の sample.txt をオープンする
                isn = this.getAssets().open("numbers.txt");
                brn = new BufferedReader(new InputStreamReader(isn));
                // １行ずつ読み込み、改行を付加する
                String str;
                while ((str = brn.readLine()) != null) {
                    dic_n[i] = str;
                    i += 1;
                }
            } finally {
                if (isn != null) isn.close();
                if (brn != null) brn.close();
            }
        } catch (Exception e){
            // エラー発生時の処理
        }


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

    //CubismのSDKのまま
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
                // 感情を変える
                expression_counter = 4;
                JniBridgeJava.changeAllExpression(expression_counter);
                textView.setText(String.valueOf(expression_counter));
//                this.expression_counter++;
                break;
            case MotionEvent.ACTION_MOVE:
                JniBridgeJava.nativeOnTouchesMoved(pointX, pointY);
                break;
        }
        return super.onTouchEvent(event);
    }
    //ここまで



    //言語処理について
    private RecognitionListener recognitionListener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle bundle) {}
        @Override
        public void onBeginningOfSpeech() {}
        @Override
        public void onRmsChanged(float v){}
        @Override
        public void onBufferReceived(byte[] bytes) {}
        @Override
        public void onEndOfSpeech() {}
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
                    countDownTimer.cancel();
                    Toast.makeText(MainActivity.this, "なにも聞き取れなかったよ...", Toast.LENGTH_LONG).show();
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    resetText();
                    textView.setText("なんていってるかわからないエラー");
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
                expression_counter = 5;
                JniBridgeJava.changeAllExpression(expression_counter);
//                textView.setText(String.valueOf(expression_counter));
                // 5と指定しても、ランダムなモーションになってしまう
            }
            if (TextUtils.equals(result[0], "これからアプリの紹介をしていきたいと思います")) {
                Toast.makeText(MainActivity.this, "ご清聴ください！！", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            if (TextUtils.equals(result[0], "お名前は")) {
                Toast.makeText(MainActivity.this, "ひよりです!!", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            if (TextUtils.equals(result[0], "何で書かれてるの")) {
                Toast.makeText(MainActivity.this, "動きはC++のSDKをJavaで呼び出している感じ!!", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            if (TextUtils.equals(result[0], "実装するの大変でした")) {
                Toast.makeText(MainActivity.this, "ごめんねー^^;", Toast.LENGTH_LONG).show();
                countDownTimer.cancel();
                countTextView.setText("");
            }
            //
            float emotion = 0f;
            System.out.println(emotion);
            for (int j = 0; j < dic_len; j++){
                if (result[0].contains(dic_w[j])){
                    emotion += Float.parseFloat(dic_n[j]);

                }
            }

            System.out.println(emotion);
            if((-5 < emotion)&&(emotion < 0)) {
                expression_counter = 8;
                JniBridgeJava.changeAllExpression(expression_counter);
                textView.setText(String.valueOf(result[0] +"\n感情値:"+emotion));

            }else if((0 <= emotion)&&(emotion < 0.5)) {
                expression_counter = 1;
                JniBridgeJava.changeAllExpression(expression_counter);
                textView.setText(String.valueOf(result[0] +"\n感情値:"+ emotion));

            }else if (emotion >= 0.5) {
                expression_counter = 6;
                JniBridgeJava.changeAllExpression(expression_counter);
                textView.setText(String.valueOf(result[0] +"\n感情値:"+ emotion));

            }else {
                expression_counter = 7;
                JniBridgeJava.changeAllExpression(expression_counter);
                textView.setText(String.valueOf(result[0] +"\n感情値:"+ emotion));

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
        countTextView.setText("エラー！");
    }

    public void countDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            count = 8;
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
