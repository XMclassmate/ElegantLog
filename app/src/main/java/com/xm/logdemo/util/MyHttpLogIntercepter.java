package com.xm.logdemo.util;


import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * Created by XMclassmate on 2018/6/1.
 */

public class MyHttpLogIntercepter implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private boolean isDetailHeader = false;

    public MyHttpLogIntercepter(boolean isDetailHeader) {
        this.isDetailHeader = isDetailHeader;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String url, result, params = null, json = null;

        StringBuffer sb = new StringBuffer();
        Request request = chain.request();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        url = request.url().url().toString();
        Connection connection = chain.connection();
        String requestStartMessage = "请求方式:" + request.method()
                + (connection != null ? " " + connection.protocol() : "") + "\n";
        sb.append(requestStartMessage);

        if (hasRequestBody) {
            if (requestBody.contentType() != null && isDetailHeader) {
                sb.append("Content-Type: " + requestBody.contentType() + "\n");
            }
            if (requestBody.contentLength() != -1 && isDetailHeader) {
                sb.append("Content-Length: " + requestBody.contentLength() + "\n");
            }
        }

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            if ("Interface-Name".equals(name)) {
                sb.append(name + ": " + headers.value(i) + "\n");
                continue;
            }
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name) && isDetailHeader) {
                sb.append(name + ": " + headers.value(i) + "\n");
            }

        }

        if (!hasRequestBody) {
            if (isDetailHeader) {
                sb.append("END " + request.method() + "\n");
            }
        } else if (bodyHasUnknownEncoding(request.headers())) {
            if (isDetailHeader) {
                sb.append("END " + request.method() + " (encoded body omitted)" + "\n");
            }
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                params = buffer.readString(charset);
                if (isDetailHeader) {
                    sb.append("END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)" + "\n");
                }
            } else if (isDetailHeader) {
                sb.append("END " + request.method() + " (binary "
                        + requestBody.contentLength() + "-byte body omitted)" + "\n");
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            sb.append("HTTP FAILED: " + e + "\n");
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        sb.append("响应码:" + response.code() + "\n"
                + "请求结果:" + (response.message().isEmpty() ? "" : ' ' + response.message()) + "\n"
                + "请求时间:" + tookMs + "ms" + (", " + bodySize + " body") + "\n");

        headers = response.headers();
        if (isDetailHeader) {
            for (int i = 0, count = headers.size(); i < count; i++) {
                sb.append(headers.name(i) + ": " + headers.value(i) + "\n");
            }
        }

        if (!HttpHeaders.hasBody(response)) {
            if (isDetailHeader) {
                sb.append("END HTTP" + "\n");
            }
        } else if (bodyHasUnknownEncoding(response.headers())) {
            if (isDetailHeader) {
                sb.append("END HTTP (encoded body omitted)" + "\n");
            }
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Long gzippedLength = null;
            if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                gzippedLength = buffer.size();
                GzipSource gzippedResponseBody = null;
                try {
                    gzippedResponseBody = new GzipSource(buffer.clone());
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                } finally {
                    if (gzippedResponseBody != null) {
                        gzippedResponseBody.close();
                    }
                }
            }

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (!isPlaintext(buffer)) {
                if (isDetailHeader) {
                    sb.append("END HTTP (binary " + buffer.size() + "-byte body omitted)" + "\n");
                }
                return response;
            }

            if (contentLength != 0) {
                json = buffer.clone().readString(charset);
            }
            if (isDetailHeader) {
                if (gzippedLength != null) {
                    sb.append("END HTTP (" + buffer.size() + "-byte, "
                            + gzippedLength + "-gzipped-byte body)");
                } else {
                    sb.append("END HTTP (" + buffer.size() + "-byte body)");
                }
            }
        }
        if (sb.toString().endsWith("\n")) {
            int index = sb.lastIndexOf("\n");
            sb.delete(index, sb.length());
        }
        result = sb.toString();
        LogUtils.printHttpResponseData(url, params, result, json);
        return response;
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }
}
