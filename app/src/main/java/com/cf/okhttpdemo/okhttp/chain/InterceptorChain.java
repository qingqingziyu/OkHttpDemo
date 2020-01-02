package com.cf.okhttpdemo.okhttp.chain;

import com.cf.okhttpdemo.okhttp.Call;
import com.cf.okhttpdemo.okhttp.HttpConnection;
import com.cf.okhttpdemo.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * @作者：陈飞
 * @说明：存储拦截器列表，并启动拦截器
 * @创建日期: 2020/1/2 11:27
 */
public class InterceptorChain {
    final List<Interceptor> interceptors;

    final int index;

    final Call call;

    HttpConnection httpConnection;


    public InterceptorChain(List<Interceptor> interceptors, int index, Call call, HttpConnection httpConnection) {
        this.interceptors = interceptors;
        this.index = index;
        this.call = call;
        this.httpConnection = httpConnection;
    }

    public Response proceed(HttpConnection httpConnection) throws IOException {
        this.httpConnection = httpConnection;
        return proceed();
    }

    public Response proceed() throws IOException {
        if (index > interceptors.size()) {
            throw new IOException("Interceptor Chain Error");
        }

        Interceptor interceptor = interceptors.get(index);

        InterceptorChain next = new InterceptorChain(interceptors, index + 1, call, httpConnection);

        Response respose = interceptor.intercept(next);

        return respose;
    }
}
