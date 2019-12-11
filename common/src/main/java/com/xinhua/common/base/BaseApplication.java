package com.xinhua.common.base;

import android.app.Application;
import android.util.Log;

import com.xinhua.common.utils.Constants;

/**
 * Created by 49944
 * Time: 2019/12/7 16:35
 * Des:
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG, "onCreate: common/BaseApplication");
    }
}
