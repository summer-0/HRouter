package com.xinhua.hrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xinhua.annotation_hrouter.HRouter;

@HRouter(path = "/app/SecondActivity")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
