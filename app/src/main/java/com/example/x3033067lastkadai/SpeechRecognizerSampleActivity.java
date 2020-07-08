/*
 * オフラインで音声認識できる
 * 言語は lang で指定
 *
 * 終了条件
 * onResults が呼び出される時
 * 無音声状態で5秒経つ(Androidの仕様)*/
package com.example.x3033067lastkadai;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class SpeechRecognizerSampleActivity {
    Context context;
    Intent intent;
    private SpeechRecognizer mRecognizer;
    private static final String TAG = "Speech";

    //コンストラクタ
    public SpeechRecognizerSampleActivity(Context context) {
        this.context = context;
    }

    //音声認識開始
    public void startSpeechRecognition() {
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        mRecognizer.setRecognitionListener(mRecognitionListener);
        setSpeechConfig();
        mRecognizer.startListening(intent);
    }

    //設定：オフラインやオンラインや言語など
    private void setSpeechConfig() {
        // 日本語で音声入力
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String lang = "ja-JP";
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, lang);
        //オフライン
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
    }

    //音声認識リスナー
    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        //音声認識の準備ができた時呼び出される
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech:" + bundle.toString());
        }

        //マイクに向かってしゃべり始めると呼び出される
        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech:");
        }

        //ボリュームが変化した時
        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "ボリューム" + v);
        }

        //音声データを返す
        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "音声データ");
        }

        //話し終わったら呼び出される
        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
        }

        //エラーが起こった時
        @Override
        public void onError(int error) {
            Log.d(TAG, "Recognition Error: " + getErrorMessage(error));
        }

        //stopListening()時に呼び出され、結果を返す
        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> values = bundle.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            for (String val : values) {
                Log.d(TAG, "onResults" + val);
            }
        }

        //認識結果が部分的に返ってきたとき
        @Override
        public void onPartialResults(Bundle bundle) {
            ArrayList<String> values = bundle.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            for (String val : values) {
                Log.d(TAG, "onPartialResults" + val);
            }
        }

        //分からない
        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent" + i + bundle);
        }
    };

    //音声認識終了
    public void stopSpeechRecognition() {
        mRecognizer.stopListening();
    }

    //エラーメッセージを取得
    private static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Other client side errors";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network related errors";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network operation timed out";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No recognition result matched";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "RecognitionService busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server sends error status";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input";
        }
        return "Unknown error";
    }
}