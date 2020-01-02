package com.cf.okhttpdemo.okhttp;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @作者：陈飞
 * @说明：http的url请求信息存储，如host，port，protocol,file
 * @创建日期: 2020/1/2 10:02
 */
public class HttpUrl {
    private String protocol;//协议，http或者https

    private String host;//服务器地址

    private String file;//请求服务器文件路径

    private int port;//服务器服务端口


    public HttpUrl(String url) throws MalformedURLException {
        URL localUrl = new URL(url);
        host = localUrl.getHost();
        protocol = localUrl.getProtocol();
        file = localUrl.getFile();
        port = localUrl.getPort();
        if (port == -1) {
            //代表url没有端口信息，就是使用默认端口，http:80,https443
            port = localUrl.getDefaultPort();
        }

        if (TextUtils.isEmpty(file)) {
            //如果为空,默认加上/
            file = "/";
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
