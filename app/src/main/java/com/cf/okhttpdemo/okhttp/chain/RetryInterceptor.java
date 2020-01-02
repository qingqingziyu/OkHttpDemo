package com.cf.okhttpdemo.okhttp.chain;

import android.util.Log;

import com.cf.okhttpdemo.okhttp.Call;
import com.cf.okhttpdemo.okhttp.Response;

import java.io.IOException;

/**
 * @作者：陈飞
 * @说明：重试拦截器
 * @创建日期: 2020/1/2 16:20
 */
public class RetryInterceptor implements Interceptor {


    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.e("interceprot", "重试拦截器");
        Call call = interceptorChain.call;
        IOException ioException = null;
        for (int i = 0; i < call.getHttpClient().getRetryTimes(); i++) {
            if (call.isCanceled()) {
                throw new IOException("这个任务已取消");
            }

            try {
                Response response = interceptorChain.proceed();
                return response;
            } catch (IOException e) {
                ioException = e;
            }
        }
        throw ioException;
    }
}
