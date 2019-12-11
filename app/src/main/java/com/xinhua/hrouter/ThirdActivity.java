package com.xinhua.hrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xinhua.annotation_hrouter.HRouter;

@HRouter(path = "/app/ThirdActivity")
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }
}
