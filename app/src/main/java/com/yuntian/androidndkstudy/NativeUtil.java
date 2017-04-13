package com.yuntian.androidndkstudy;

import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * description 提供native方法.
 * Created by ChuYingYan on 2017/3/30.
 */

public class NativeUtil {

    private static final String TAG = "NativeUtil";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        initIDs();
    }

    public static native void initIDs();
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    public static native String staticJNIRegister(String  str);

    public static native String dynamicJNIRegister(String  str);



    public static  native void startMonitor();

    public static native void stopMonitor();


    public static native void encodeArray(int[] arr);


    public static native byte[] getRandom();


    public static void init(){
        ALog.d("NativeUtil 初始化完成");
    };


    public static void show( int pressure){
        Log.d(TAG, "show: " + pressure);
        EventBus.getDefault().post(new MessageEvent<String>("pressure",String.valueOf(pressure)));
    }

}
