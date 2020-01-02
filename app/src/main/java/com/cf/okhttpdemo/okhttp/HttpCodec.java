package com.cf.okhttpdemo.okhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @作者：陈飞
 * @说明：请求工具类
 * @创建日期: 2020/1/2 14:05
 */
public class HttpCodec {

    static final String CRLF = "\r\n";
    //回车的ASCII码
    static final int CR = 13;
    //换行的ASCII码
    static final int LF = 10;
    //一个空格
    static final String SPACE = " ";
    //http版本信息
    static final String HTTP_VERSION = "HTTP/1.1";
    //冒号
    static final String COLON = ":";


    public static final String HEAD_HOST = "Host";

    public static final String HEAD_CONNECTION = "Connetion";

    public static final String HEAD_CONNECTION_TYPE = "Content-Type";

    public static final String HEAD_CONTENT_LENGTH = "Content-Length";

    public static final String HEAD_TRANSFER_ENCODING = "Transer-Encoding";


    public static final String HEAD_VALUE_KEEP_ALIVE = "Keep-Alive";

    private static final String HEAD_VALUE_CHUNKED = "chunked";


    public static final String PROTOCOL_HTTPS = "https";

    public static final String PROTOCOL_HTTP = "http";


    public static final String ENCODE = "UTF-8";


    private final ByteBuffer byteBuffer;


    public HttpCodec() {
        this.byteBuffer = ByteBuffer.allocate(10 * 1024);
    }

    /**
     * @作者：陈飞
     * @说明：拼接request数据流，写入到socket通道
     * @创建日期: 2020/1/2 14:20
     */
    public void writeRequest(OutputStream os, Request request) throws IOException {
        StringBuffer sb = new StringBuffer();
        //get 请求方法，默认为get
        sb.append(request.getMethod());
        //一个空格
        sb.append(SPACE);
        sb.append(request.getHttpUrl().getFile());
        //一个空格
        sb.append(SPACE);
        //设置版本
        sb.append(HTTP_VERSION);
        //一个回车换行
        sb.append(CRLF);

        //拼接请求头

        Map<String, String> headers = request.getHeaders();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            //map中的key值，例如
            sb.append(entry.getKey());
            //一个冒号
            sb.append(COLON);
            //一个空格
            sb.append(SPACE);
            //map中的value值
            sb.append(entry.getValue());
            //最后一个跟一个回车键换行
            sb.append(CRLF);
        }

        //请求头最后，还需要跟一回车和换行
        sb.append(CRLF);

        //拼接请求体

        RequestBody requestBody = request.getRequestBody();

        if (requestBody != null) {
            sb.append(requestBody.getBody());
        }

        os.write(sb.toString().getBytes());
        os.flush();
    }


    /**
     * @作者：陈飞
     * @说明：读取服务器返回回来的一行数据
     * @创建日期: 2020/1/2 14:33
     */
    public String readLine(InputStream is) throws IOException {

        //先把byteBuffer清理下
        byteBuffer.clear();
        //然后标记下
        byteBuffer.mark();

        //可能行结束的标志，当出现一个r的时候，置为true，如果下一个是/n,就是行结束了
        boolean isMaybeEofLine = false;

        byte b;

        while ((b = (byte) is.read()) != -1) {
            byteBuffer.put(b);
            if (b == CR) {//如果读到一个r
                isMaybeEofLine = true;
            } else if (isMaybeEofLine) {
                if (b == LF) {//如果读到一个n，意味着，行结束了
                    byte[] lineBytes = new byte[byteBuffer.position()];
                    //然后重置byteBuffer
                    byteBuffer.reset();//与marke搭配使用，告诉bytebuffer，使用者将要拿出mark到当前保存的字节数据
                    byteBuffer.get(lineBytes);

                    byteBuffer.clear();//清空
                    byteBuffer.mark();
                    return new String(lineBytes, ENCODE);
                }
                //如果下一个字节不是/n，把标志重置为false
                isMaybeEofLine = false;
            }
        }
        throw new IOException("Respone read line error");
    }

    /**
     * @作者：陈飞
     * @说明：读取服务器返回的响应头
     * @创建日期: 2020/1/2 15:13
     */
    public Map<String, String> readHeaders(InputStream is) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        while (true) {
            String line = readLine(is);

            if (isEmptyLine(line)) {
                //如果读到空行 \r\n 响应头读完了
                break;
            }

            //因为服务器返回的响应头的格式也是key：value的格式
            int index = line.indexOf(":");

            if (index > 0) {
                String key = line.substring(0, index);
                //这里追加2 因为，value前面还有冒号和空格，所以，value的第一个位置，需要往后移

                //减2是因为line后面有/r/n两个字节
                String value = line.substring(index + 2, line.length() - 2);

                headers.put(key, value);
            }
        }
        return headers;
    }

    /**
     * @作者：陈飞
     * @说明：根据长度读取字节数据
     * @创建日期: 2020/1/2 15:26
     */
    public byte[] readBytes(InputStream is, int length) throws IOException {
        byte[] bytes = new byte[length];
        int readNum = 0;
        while (true) {
            readNum = is.read(bytes, readNum, length - readNum);
            if (readNum == length) {
                return bytes;
            }
        }
    }


    /**
     * @作者：陈飞
     * @说明：服务器传输响应体的方式为分块方式，根据分块的方式获取响应体
     * @创建日期: 2020/1/2 15:28
     */
    public String readChunked(InputStream is, int length) throws IOException {
        int len = -1;

        boolean isEmptyData = false;

        StringBuffer chunked = new StringBuffer();

        while (true) {
            if (len < 0) {
                //获取块的长度
                String line = readLine(is);
                length += line.length();
                //去掉/r/n
                line = line.substring(0, line.length() - 2);
                //获得长度，16进制字符串转换成10进制整数
                len = Integer.valueOf(line, 16);

                //读到是0 ，再读一个/r/n就结束了
                isEmptyData = len == 0;
            } else {
                length += (len + 2);
                //读的时候，加上2，/r/n
                byte[] bytes = readBytes(is, len + 2);
                chunked.append(new String(bytes, ENCODE));
                len = -1;
                if (isEmptyData) {
                    return chunked.toString();
                }
            }
        }

    }

    /**
     * @作者：陈飞
     * @说明：判断是否为空行，如果读到的是/r/n，就意味是空行
     * @创建日期: 2020/1/2 15:16
     */
    private boolean isEmptyLine(String line) {
        return TextUtils.equals(line, CRLF);
    }
}
