package com.cf.okhttpdemo.okhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * @作者：陈飞
 * @说明：创建与服务器连接的socket
 * @创建日期: 2020/1/2 11:26
 */
public class HttpConnection {

    Socket socket;

    long lastUseTime;

    private Request request;

    private InputStream inputStream;

    private OutputStream outputStream;


    public void setRequest(Request request) {
        this.request = request;
    }


    public void updateLastUseTime() {
        lastUseTime = System.currentTimeMillis();
    }

    public boolean isSameAddress(String host, int port) {
        if (socket == null) {
            return false;
        }

        return TextUtils.equals(request.getHttpUrl().getHost(), host) && request.getHttpUrl().getPort() == port;
    }

    /**
     * @作者：陈飞
     * @说明：创建Socket连接
     * @创建日期: 2020/1/2 14:01
     */
    private void createSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            HttpUrl httpUrl = request.getHttpUrl();
            //创建socket
            if (httpUrl.getProtocol().equalsIgnoreCase(HttpCodec.PROTOCOL_HTTPS)) {
                //如果是https,就需要jdk默认的SSLSocketFactory来创建socket
                socket = SSLSocketFactory.getDefault().createSocket();
            } else {
                socket = new Socket();
            }

            socket.connect(new InetSocketAddress(httpUrl.getHost(), httpUrl.getPort()));
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
    }

    /**
     * @作者：陈飞
     * @说明：关闭socket连接
     * @创建日期: 2020/1/2 15:43
     */
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public InputStream call(HttpCodec httpCodec) throws IOException {
        //创建socket
        createSocket();
        //发送请求
        httpCodec.writeRequest(outputStream, request);
        //返回服务器响应
        return inputStream;
    }

}
