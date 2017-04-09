package com.yuntian.androidndkstudy;

import android.content.Context;

/**
 * description  .
 * Created by ChuYingYan on 2017/3/31.
 */

public class AppContext {


    private static Context mContext;

    private AppContext() {

    }

    public static AppContext getInstance(Context context) {
        mContext=context;
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static AppContext INSTANCE = new AppContext();
    }

    public static void init(Context context) {
       mContext = context;
    }


    public static Context getApplicationContext() {

        return mContext;
    }
}
