package link.ttiot.broker.handler.http;

import io.netty.handler.codec.http.HttpRequestDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.io.BufferUtil.lineEnd;


public class HttpHeaderDecoder {

    public static final String EMPTY = "";

    public static enum Step {
        firstLine, header, body
    }

    private static Logger log = LoggerFactory.getLogger(HttpRequestDecoder.class);

    /**
     * 头部，最多有多少字节
     */
    public static final int MAX_LENGTH_OF_HEADER = 20480;

    /**
     * 头部，每行最大的字节数
     */
    public static final int MAX_LENGTH_OF_HEADER_LINE = 2048;


    public static HttpPacket decode(ByteBuffer buffer, boolean isBody) {
        int initPosition = buffer.position();
        int readableLength = buffer.limit() - initPosition;
        //		int count = 0;
        Step step = Step.firstLine;
        //		StringBuilder currLine = new StringBuilder();
        Map<String, String> headers = new HashMap<>();
        int contentLength = 0;
        byte[] bodyBytes = null;
        StringBuilder headerSb = new StringBuilder(512);
        RequestLine firstLine = null;

        while (buffer.hasRemaining()) {
            String line = readLine(buffer, null, MAX_LENGTH_OF_HEADER_LINE);

            int newPosition = buffer.position();
            if (newPosition - initPosition > MAX_LENGTH_OF_HEADER) {
                throw new RuntimeException("max http header length " + MAX_LENGTH_OF_HEADER);
            }

            if (line == null) {
                return null;
            }

            headerSb.append(line).append("\r\n");
            //头部解析完成了

            if (step == Step.firstLine) {
                firstLine = parseRequestLine(line);
                step = Step.header;
            } else if (step == Step.header) {
                //不解析包体的话,结束(换句话说就是只解析请求行与请求头)
                if ("".equals(line) && !isBody) {
                    break;
                }
                KeyValue keyValue = parseHeaderLine(line);
                headers.put(keyValue.getKey(), keyValue.getValue());
            }
            continue;
        }

        HttpPacket httpRequest = new HttpPacket();
        httpRequest.setHeaderString(headerSb.toString());
        httpRequest.setHeaders(headers);
        return httpRequest;
    }


    public static String readLine(ByteBuffer buffer, String charset, Integer maxlength) {

        int startPosition = buffer.position();
        int endPosition = lineEnd(buffer, maxlength);

        if (endPosition > startPosition) {
            byte[] bs = new byte[endPosition - startPosition];
            System.arraycopy(buffer.array(), startPosition, bs, 0, bs.length);
            if (StringUtils.isNotBlank(charset)) {
                try {
                    return new String(bs, charset);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return new String(bs);
            }

        } else if (endPosition == -1) {
            return null;
        } else if (endPosition == startPosition) {
            return "";
        }
        return null;
    }


    public static KeyValue parseHeaderLine(String line) {
        KeyValue keyValue = new KeyValue();
        int p = line.indexOf(":");
        if (p == -1) {
            keyValue.setKey(line);
            return keyValue;
        }

        String name = StringUtils.lowerCase(line.substring(0, p).trim());
        String value = line.substring(p + 1).trim();

        keyValue.setKey(name);
        keyValue.setValue(value);

        return keyValue;
    }


    public static RequestLine parseRequestLine(String line) {
        try {
            int index1 = line.indexOf(' ');
            String _method = StringUtils.upperCase(line.substring(0, index1));
            Method method = Method.from(_method);
            int index2 = line.indexOf(' ', index1 + 1);
            // "/user/get?name=999"
            String pathAndQueryStr = line.substring(index1 + 1, index2);
            //"/user/get"
            String path = null;
            String queryStr = null;
            int indexOfQuestionMark = pathAndQueryStr.indexOf("?");
            if (indexOfQuestionMark != -1) {
                queryStr = StringUtils.substring(pathAndQueryStr, indexOfQuestionMark + 1);
                path = StringUtils.substring(pathAndQueryStr, 0, indexOfQuestionMark);
            } else {
                path = pathAndQueryStr;
                queryStr = "";
            }

            String protocolVersion = line.substring(index2 + 1);
            String[] pv = StringUtils.split(protocolVersion, "/");
            String protocol = pv[0];
            String version = pv[1];

            RequestLine requestLine = new RequestLine();
            requestLine.setMethod(method);
            requestLine.setPath(path);
            requestLine.setInitPath(path);
            requestLine.setPathAndQuery(pathAndQueryStr);
            requestLine.setQuery(queryStr);
            requestLine.setVersion(version);
            requestLine.setProtocol(protocol);
            requestLine.setLine(line);

            return requestLine;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


}
