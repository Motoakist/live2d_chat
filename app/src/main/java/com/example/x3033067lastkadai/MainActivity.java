package com.example.x3033067lastkadai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button) findViewById(R.id.addButton);
//        addButton.setOnClickListener(this);


        // クリックイベントを取得したいボタン
        Button button = (Button) findViewById(R.id.mikeButton);

// ボタンに OnClickListener インターフェースを実装する
        button.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
//                SpeechRecognizerSampleActivity.startSpeechRecognition();
            }
        });


    }
}