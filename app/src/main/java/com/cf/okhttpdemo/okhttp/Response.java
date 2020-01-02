package com.cf.okhttpdemo.okhttp;

import java.util.HashMap;
import java.util.Map;

/**
 *@作者：陈飞
 *@说明：网络请求返回的数据
 *@创建日期: 2020/1/2 11:18
 */
public class Response {

    //状态码
    int code;

    //返回包的长度
    int contentLenght = -1;

    //返回包头信息
    Map<String, String> headers = new HashMap<>();

    //包的内容
    String body;

    //是否保持连接
    boolean inKeepAlive;

    public Response() {
    }

    public Response(int code, int contentLenght, Map<String, String> headers, String body, boolean inKeepAlive) {
        this.code = code;
        this.contentLenght = contentLenght;
        this.headers = headers;
        this.body = body;
        this.inKeepAlive = inKeepAlive;
    }

    public int getCode() {
        return code;
    }

    public int getContentLenght() {
        return contentLenght;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean isInKeepAlive() {
        return inKeepAlive;
    }
}
