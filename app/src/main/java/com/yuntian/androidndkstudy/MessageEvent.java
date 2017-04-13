package com.yuntian.androidndkstudy;

/**
 * description  .
 * Created by ChuYingYan on 2017/4/13.
 */

public class MessageEvent<T> {


    private  String tag;


    private  T o;


    public MessageEvent(String tag, T o) {
        this.tag = tag;
        this.o = o;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getO() {
        return o;
    }

    public void setO(T o) {
        this.o = o;
    }
}
