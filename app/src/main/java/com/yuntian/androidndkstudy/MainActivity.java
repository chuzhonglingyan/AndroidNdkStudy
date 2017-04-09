package com.yuntian.androidndkstudy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_static_jni)
    TextView tvStaticJni;
    @BindView(R.id.tv_dynamic_jni)
    TextView tvDynamicJni;
    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    MainHandler   mMainHandler;

    static MainHandler mHandler;
    int[] array = {1,2,3,4,5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainHandler=new MainHandler();
        mHandler=mMainHandler;
    }

    @OnClick({R.id.tv_static_jni, R.id.tv_dynamic_jni, R.id.tv_start, R.id.tv_stop,R.id.tv_encode,R.id.tv_rand})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_static_jni:
                ToastUtils.showShortToast(NativeUtil.staticJNIRegister("静态注册 来自Java的问候"));
                break;
            case R.id.tv_dynamic_jni:
                ToastUtils.showShortToast(NativeUtil.dynamicJNIRegister("动态注册 来自Java的问候"));
                break;
            case R.id.tv_start:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NativeUtil.startMonitor();
                    }
                }).start();
                break;
            case R.id.tv_stop:
                NativeUtil.stopMonitor();
                break;
            case R.id.tv_encode:
                NativeUtil.encodeArray(array);
                LogUtil.d(TAG,""+ Arrays.toString(array));
                break;
            case R.id.tv_rand:
                ToastUtils.showShortToast(Base64.encodeToString(NativeUtil.getRandom(), Base64.DEFAULT));
                LogUtil.d(TAG,""+Base64.encodeToString(NativeUtil.getRandom(), Base64.DEFAULT));
                break;
        }
    }


    public static void show(final int pressure) {
        Message tMsg = new Message();
        Bundle tBundle = new Bundle();
        tBundle.putString("CMD", pressure+"");
        tMsg.setData(tBundle);
        mHandler.sendMessage(tMsg);
        Log.d(TAG, "show: " + pressure);
    }


    class MainHandler extends Handler {
        public MainHandler() {
        }

        public MainHandler(Looper L) {
            super(L);
        }

        public void handleMessage(Message nMsg) {
            super.handleMessage(nMsg);

            Bundle tBundle = nMsg.getData();
            String tCmd = tBundle.getString("CMD");
            tvShow.setText(tCmd);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NativeUtil.stopMonitor();
    }
}
