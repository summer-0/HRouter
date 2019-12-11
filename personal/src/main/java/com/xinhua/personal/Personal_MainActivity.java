package com.xinhua.personal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xinhua.annotation_hrouter.HRouter;
import com.xinhua.common.base.BaseActivity;

@HRouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity__main);
    }
}
