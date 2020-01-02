package com.cf.okhttpdemo.okhttp;

import android.telecom.Call;

import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 *@作者：陈飞
 *@说明：网络请求任务调度器，通过线程池来控制
 *@创建日期: 2020/1/2 9:17
 */
public class Dispather {

    //最多同时请求的数量
    private int maxRequest;

    //同一个Host最多允许请求的数量
    private int maxRequestPreHost;


    //创建线程池
    private ExecutorService executorService;

    private final Deque<Call>

    public Dispather() {
        this(64,5);
    }

    public Dispather(int maxRequest, int maxRequestPreHost) {
        this.maxRequest = maxRequest;
        this.maxRequestPreHost = maxRequestPreHost;
    }




}
