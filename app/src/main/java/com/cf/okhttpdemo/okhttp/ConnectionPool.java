package com.cf.okhttpdemo.okhttp;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @作者：陈飞
 * @说明：与服务器之间的socket连接池
 * @创建日期: 2020/1/2 13:14
 */
public class ConnectionPool {

    private long keepAliveTime;

    private Deque<HttpConnection> httpConnections = new ArrayDeque<>();

    private boolean cleanupRunning;

    //线程池
    private static final Executor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "这是一个线程连接池");
            //设置为守护线程
            thread.setDaemon(true);
            return thread;
        }
    });

    public ConnectionPool() {
        this(60L, TimeUnit.SECONDS);
    }

    public ConnectionPool(long keepAliveTime, TimeUnit timeUnit) {
        this.keepAliveTime = timeUnit.toMillis(keepAliveTime);
    }


    /**
     * @作者：陈飞
     * @说明：生成一个清理线程，这个线程会定期去检查，并且清理那些无用的连接，这里的无用是指没有使用的间期超过了保留时间
     * @创建日期: 2020/1/2 13:27
     */
    private Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long now = System.currentTimeMillis();
                //获取到下次检测时间
                long waitDuration = cleanup(now);

                if (waitDuration == -1) {
                    return;//连接池为空，清理线程结束
                }

                //说明有线程池,不需要
                if (waitDuration > 0) {

                    synchronized (ConnectionPool.this) {
                        try {
                            //等待线程
                            ConnectionPool.this.wait(waitDuration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    /**
     * @作者：陈飞
     * @说明：根据当前时间，清理无用的连接
     * @创建日期: 2020/1/2 13:41
     */
    private long cleanup(long now) {

        long longestIdleDuration = -1;//最长的闲置时间

        synchronized (this) {
            Iterator<HttpConnection> connectionIterator = httpConnections.iterator();
            while (connectionIterator.hasNext()) {
                //获得连接
                HttpConnection httpConnection = connectionIterator.next();
                //计算闲置时间
                long idleDuration = now - httpConnection.lastUseTime;

                //根据闲置时间来判断是都需要被清理
                if (idleDuration > keepAliveTime) {//已超过时间，那么关闭
                    connectionIterator.remove();
                    httpConnection.close();
                    Log.e("连接池", "超过闲置时间，移出连接池");
                    continue;
                }

                //然后判断出整个连接池中最大的闲置时间
                if (idleDuration > longestIdleDuration) {
                    longestIdleDuration = idleDuration;
                }
            }

            if (longestIdleDuration >= 0) {
                //返回键的值，可以让清理线程知道，下一次清理要多久以后
                return keepAliveTime - longestIdleDuration;
            }

            //如果运行到这里的话，代表 longestIdleDuration =-1，连接池中为空
            cleanupRunning = false;

            return longestIdleDuration;
        }
    }

    /**
     *@作者：陈飞
     *@说明：加入连接池
     *@创建日期: 2020/1/2 15:58
     */
    public void putHttpConnection(HttpConnection httpConnection) {
        //判断线程池有没有在运行
        if (!cleanupRunning) {
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        httpConnections.add(httpConnection);
    }

    /**
     * @作者：陈飞
     * @说明：根据服务器地址与端口，来获取可复用的连接
     * @创建日期: 2020/1/2 15:55
     */
    public synchronized HttpConnection getHttpConnection(String host, int port) {
        Iterator<HttpConnection> httpConnectionIterator = httpConnections.iterator();
        while (httpConnectionIterator.hasNext()) {
            HttpConnection httpConnection = httpConnectionIterator.next();
            if (httpConnection.isSameAddress(host, port)) {
                httpConnectionIterator.remove();
                return httpConnection;
            }
        }
        return null;
    }


}
