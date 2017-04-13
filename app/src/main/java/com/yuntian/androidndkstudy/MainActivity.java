package com.yuntian.androidndkstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * description 测试jni调用的demo类.
 * Created by ChuYingYan on 2017/4/10.
 */
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

    int[] array = {1,2,3,4,5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.tv_static_jni, R.id.tv_dynamic_jni, R.id.tv_start, R.id.tv_stop,R.id.tv_encode,R.id.tv_rand})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_static_jni:  //jni方法的静态注册
                long startTime1 = System.nanoTime();  //開始時間
                ToastUtils.showShortToast(NativeUtil.staticJNIRegister("静态注册 来自Java的问候"));
                double consumingTime1 = (double) (System.nanoTime()-startTime1)/100000; //消耗時間
                ALog.d(String.valueOf(consumingTime1)+"毫秒");
                break;
            case R.id.tv_dynamic_jni: //jni方法的静态注册
                long startTime2= System.nanoTime();  //開始時間
                ToastUtils.showShortToast(NativeUtil.dynamicJNIRegister("动态注册 来自Java的问候"));
                double consumingTime2 = (double) (System.nanoTime()-startTime2)/100000; //消耗時間
                ALog.d(String.valueOf(consumingTime2)+"毫秒");
                break;
            case R.id.tv_start: //开始线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NativeUtil.startMonitor();
                    }
                }).start();
                break;
            case R.id.tv_stop://停止线程
                NativeUtil.stopMonitor();
                break;
            case R.id.tv_encode: //java数组传到C层改变，返回到java层输出
                NativeUtil.encodeArray(array);
                ALog.d(Arrays.toString(array));
                break;
            case R.id.tv_rand:  //调用opensll的随机数方法
                long startTime = System.nanoTime();  //開始時間
                ToastUtils.showShortToast(Base64.encodeToString(NativeUtil.getRandom(), Base64.DEFAULT));
                ALog.d(Base64.encodeToString(NativeUtil.getRandom(), Base64.DEFAULT));
                double consumingTime = (double) (System.nanoTime()-startTime)/100000; //消耗時間
                ALog.d(String.valueOf(consumingTime)+"毫秒");
                break;
        }
    }



    @Subscribe(threadMode = ThreadMode.MainThread)
    public void showPressure(MessageEvent<String> message) {
        if (message.getTag().equals("pressure"))
        tvShow.setText(message.getO());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        NativeUtil.stopMonitor();
        EventBus.getDefault().unregister(this);
    }
}
