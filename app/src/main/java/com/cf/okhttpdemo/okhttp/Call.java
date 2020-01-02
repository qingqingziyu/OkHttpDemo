package com.cf.okhttpdemo.okhttp;

import com.cf.okhttpdemo.okhttp.chain.Interceptor;

import java.util.ArrayList;

/**
 * @作者：陈飞
 * @说明：使用者通过使用这个类对象来进行网络的访问
 * @创建日期: 2020/1/2 9:41
 */
public class Call {

    private HttpClient httpClient;

    private Request request;

    //是否被执行过
    private boolean executed;

    //是否被取消了
    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

    public Request getRequest() {
        return request;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * @作者：陈飞
     * @说明：获取返回
     * @创建日期: 2020/1/2 16:18
     */
    Response getResponse() {
        //添加拦截器集合
        ArrayList<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(httpClient.getInterceptors());
    }
}
