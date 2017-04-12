package com.yuntian.androidndkstudy;

import android.app.Application;


/**
 * description Application类.
 * Created by ChuYingYan on 2017/4/10.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NativeUtil.init();
        AppContext.init(this);
        new ALog.Builder(this)
                .setLogSwitch(BuildConfig.DEBUG);// 设置log总开关，默认开
    }
}
