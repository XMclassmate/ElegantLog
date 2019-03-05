package com.xm.logdemo;

import android.app.Application;

import com.xm.logdemo.util.LogUtils;

/**
 * Created by XMclassmate on 2018/10/31
 */
public class MyApplication extends Application {

    private static MyApplication ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        LogUtils.init(BuildConfig.DEBUG, getString(R.string.app_name));
    }

    public static MyApplication getCtx(){
        return ctx;
    }

}
