package com.example.back4app.barbershop_lesson1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseInstallation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
