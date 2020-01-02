package com.cf.okhttpdemo.okhttp;

import com.cf.okhttpdemo.okhttp.chain.Interceptor;

import java.util.List;

/**
 * @作者：陈飞
 * @说明：HttpClient对象
 * @创建日期: 2020/1/2 9:14
 */
public class HttpClient {

    //设置调度器
    private Dispather dispather;

    //拦截器
    private List<Interceptor> interceptors;

    //重试时间
    private int retryTimes;

    //连接池
    private ConnectionPool connectionPool;

    public int getRetryTimes() {
        return retryTimes;
    }

    public Dispather getDispather() {
        return dispather;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public Call newCall(Request request) {
        return new Call(this, request);
    }

    public HttpClient(Builder builder) {
        this.dispather = builder.dispather;
        this.interceptors = builder.interceptors;
        this.retryTimes = builder.retryTimes;
        this.connectionPool = builder.connectionPool;
    }

    //建造对象
    public static final class Builder {
        //设置调度器
        private Dispather dispather;

        //拦截器
        private List<Interceptor> interceptors;

        //重试时间
        private int retryTimes;

        //连接池
        private ConnectionPool connectionPool;

        private Builder addInterceptors(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public Builder setDispather(Dispather dispather) {
            this.dispather = dispather;
            return this;
        }

        public Builder setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public Builder setConnectionPool(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
            return this;
        }

        public HttpClient builder() {
            if (dispather == null) {
                dispather = new Dispather();
            }

            //创建连接池
            if (connectionPool == null) {
                connectionPool = new ConnectionPool();
            }
            return new HttpClient(this);
        }
    }


}
