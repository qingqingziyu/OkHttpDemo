package com.cf.okhttpdemo.okhttp;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @作者：陈飞
 * @说明：在post请求中，需要有requestBody格式的参数，此类存储post请求中的参数的数据
 * @创建日期: 2020/1/2 10:16
 */
public class RequestBody {

    //表单提交，使用urllencoded编码
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static final String CHARSET = "UTF-8";

    Map<String, String> encodeBodys = new HashMap<>();

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public int getContentLength() {
        return getBody().getBytes().length;
    }

    public String getBody() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> enty : encodeBodys.entrySet()) {
            sb.append(enty.getKey())
                    .append("=")
                    .append(enty.getValue())
                    .append("&");
        }
        if (sb.length() != 0) {
            //这是为了删除最后一个&，以为后面不再增加参数
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    //通过JDK的url编码
    public RequestBody add(String key, String value) {
        try {
            encodeBodys.put(URLEncoder.encode(key, CHARSET), URLEncoder.encode(value, CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
