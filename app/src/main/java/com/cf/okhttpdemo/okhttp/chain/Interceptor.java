package com.cf.okhttpdemo.okhttp.chain;

import com.cf.okhttpdemo.okhttp.Response;

import java.io.IOException;

/**
 * @作者：陈飞
 * @说明：拦截器接口
 * @创建日期: 2020/1/2 11:23
 */
public interface Interceptor {

    Response intercept(InterceptorChain interceptorChain) throws IOException;
}
