package com.yuntian.androidndkstudy;

import android.app.Application;

/**
 * Created by Administrator on 2017/3/30.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NativeUtil.init();
        AppContext.init(this);
    }
}
