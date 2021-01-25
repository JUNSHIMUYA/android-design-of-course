package com.example.android_design;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.android_design.db.CityCode;
import com.example.android_design.util.DBAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "save";  //文件名
    public static int MODE = MODE_PRIVATE;  //操作模式
    public DBAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=new DBAdapter(this);
        db.open();



        setContentView(R.layout.activity_main);
        SharedPreferences preferences=getSharedPreferences(PREFERENCE_NAME, MODE);
        if(preferences.getString("weather",null)!=null)
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
