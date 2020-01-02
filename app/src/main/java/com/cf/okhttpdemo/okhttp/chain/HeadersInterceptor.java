package com.cf.okhttpdemo.okhttp.chain;

import android.util.Log;

import com.cf.okhttpdemo.okhttp.HttpClient;
import com.cf.okhttpdemo.okhttp.HttpCodec;
import com.cf.okhttpdemo.okhttp.Request;
import com.cf.okhttpdemo.okhttp.Response;

import java.io.IOException;
import java.util.Map;

/**
 * @作者：陈飞
 * @说明：请求头拦截器
 * @创建日期: 2020/1/2 16:41
 */
public class HeadersInterceptor implements Interceptor {

    @Override
    public Response intercept(InterceptorChain interceptorChain) throws IOException {
        Log.e("interceprot", "Http头拦截器 ...");

        Request request = interceptorChain.call.getRequest();
        Map<String, String> headers = request.getHeaders();
        if (!headers.containsKey(HttpCodec.HEAD_HOST)) {
            headers.put(HttpCodec.HEAD_HOST, request.getHttpUrl().getHost());
        }
        if (!headers.containsKey(HttpCodec.HEAD_CONNECTION)) {
            headers.put(HttpCodec.HEAD_CONNECTION, HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        }

        //如果有body
        if (null != request.getRequestBody()) {
            String contentType = request.getRequestBody().getContentType();
            if (contentType != null) {
                headers.put(HttpCodec.HEAD_CONNECTION_TYPE, contentType);
            }
            long contentLength = request.getRequestBody().getContentLength();

            if (contentLength != -1) {
                headers.put(HttpCodec.HEAD_CONTENT_LENGTH, Long.toString(contentLength));
            }
        }

        return interceptorChain.proceed();
    }


}
