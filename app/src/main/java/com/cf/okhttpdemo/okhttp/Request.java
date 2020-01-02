package com.cf.okhttpdemo.okhttp;

import android.net.Uri;
import android.os.Build;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @作者：陈飞
 * @说明：存储我们网络请求的一些参数的类,建造者模式
 * @创建日期: 2020/1/2 9:49
 */
public class Request {

    private Map<String, String> headers;//http请求头

    private String method;//请求方法，post或者get方法

    private HttpUrl httpUrl;//http的url信息

    private RequestBody requestBody; //如果是post请求，还会有requestBody参数存储信息


    public Request(Builder builder) {
        this.headers = builder.headers;
        this.method = builder.method;
        this.httpUrl = builder.httpUrl;
        this.requestBody = builder.requestBody;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    /**
     *@说明：建造者
     *@创建日期: 2020/1/2 10:56
     */
    public static final class Builder {

        private Map<String, String> headers;//http请求头

        private String method;//请求方法，post或者get方法

        private HttpUrl httpUrl;//http的url信息

        private RequestBody requestBody; //如果是post请求，还会有requestBody参数存储信息

        //添加
        private Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        //删除指定
        public Builder removeHeader(String key) {
            headers.remove(key);
            return this;
        }


        public Builder post(RequestBody requestBody) {
            this.requestBody = requestBody;
            this.method = "POST";
            return this;
        }


        public Builder get() {
            this.method = "GET";
            return this;
        }

        public Builder setHttpUrl(String url) {

            try {
                this.httpUrl = new HttpUrl(url);
                return this;
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Http Url 格式化错误!", e);
            }
        }

        public Request builder() {
            if (httpUrl == null) {
                throw new IllegalStateException("url 为空");
            }

            //如果没有请求方法，默认设置为GET请求
            if (method == null) {
                method = "GET";
            }

            return new Request(this);
        }
    }
}
