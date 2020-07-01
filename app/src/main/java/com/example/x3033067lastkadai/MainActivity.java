package com.example.x3033067lastkadai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);

        val recordAudioPermission = android.Manifest.permission.RECORD_AUDIO
        val currentPermissionState = Context
        Compat.checkSelfPermission(context, recordAudioPermission)
        if (currentPermissionState != Packag
        eManager.PERMISSION_GRANTED) {
            if (Acti
            vityCompat.shouldShowRequestPermissionRationale(context as Activity, recordAudioPermission)) {
                // 拒否した場合
                permissionState = false
            } else {
                // 許可した場合
                ActivityC
            ompat.requestPermissions(context, arrayOf(recordAudioPermission), 1)
                permissionState = true
            }
        }
    }
}