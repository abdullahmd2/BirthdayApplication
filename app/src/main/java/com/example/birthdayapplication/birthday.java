package com.example.birthdayapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class birthday extends AppCompatActivity {

    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        mText = (TextView)findViewById(R.id.text);
        String value = getIntent().getStringExtra("birthday name");

        Log.d("name",value);
        mText.setText("Happy Birthday\n"+value);
    }
}