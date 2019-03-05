package com.xm.logdemo.util;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Created by XMclassmate on 2018/4/2.
 * 日志打印实现类
 */
public class LogPrinterImpl implements LogPrinter {

    private static boolean allowLog = true;
    private static LogPrinterImpl instance;
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Boolean> localPrintThread = new ThreadLocal<>();
    private final ThreadLocal<Boolean> localPrintStack = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char VERTICAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static String LOG_FLODER_NAME = "AppLog";

    private LogPrinterImpl() {
    }

    public static LogPrinterImpl newInstance() {
        if (instance == null) {
            synchronized (LogPrinterImpl.class) {
                if (instance == null) {
                    instance = new LogPrinterImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void setAllowLog(@Nullable boolean allowLog) {
        LogPrinterImpl.allowLog = allowLog;
        if (LogPrinterImpl.allowLog) {
            e("开启了Log");
        } else {
            log(Log.ERROR, getTag(), "关闭了Log", null);
        }
    }

    @Override
    public LogPrinter tag(@NonNull String tag) {
        if (localTag != null) {
            localTag.set(tag);
        }
        return this;
    }

    @Override
    public LogPrinter printThread(@NonNull boolean isPrintThread) {
        if (localPrintThread != null) {
            localPrintThread.set(isPrintThread);
        }
        return this;
    }

    @Override
    public LogPrinter printStack(@NonNull boolean isPrintStack) {
        if (localPrintStack != null) {
            localPrintStack.set(isPrintStack);
        }
        return this;
    }

    @Override
    public LogPrinter methodCount(@NonNull int methodCount) {
        if (localMethodCount != null) {
            localMethodCount.set(methodCount);
        }
        return this;
    }

    @Override
    public void setSDLogFloderName(String floderName) {
        if (TextUtils.isEmpty(floderName)) {
            return;
        }
        LOG_FLODER_NAME = floderName;
        log(Log.ERROR, getTag(), "LOG_FLODER_NAME:" + floderName, null);
    }

    @Override
    public void d(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.DEBUG, getTag(), msg, null);
        }
    }

    @Override
    public void d(@Nullable Object object) {
        if (allowLog) {
            log(Log.DEBUG, getTag(), toString(object), null);
        }
    }

    @Override
    public void e(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.ERROR, getTag(), msg, null);
        }
    }

    @Override
    public void e(@NonNull Throwable throwable) {
        if (allowLog) {
            log(Log.ERROR, getTag(), null, throwable);
        }
    }

    @Override
    public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.ERROR, getTag(), msg, throwable);
        }
    }

    @Override
    public void w(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.WARN, getTag(), msg, null);
        }
    }

    @Override
    public void i(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.INFO, getTag(), msg, null);
        }
    }

    @Override
    public void v(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.VERBOSE, getTag(), msg, null);
        }
    }

    @Override
    public void wtf(@NonNull String message, @Nullable Object... args) {
        if (allowLog) {
            String msg = getMsg(message, args);
            log(Log.ASSERT, getTag(), msg, null);
        }
    }

    @Override
    public void json(int priority, @Nullable String json) {
        if (allowLog) {
            log(priority, getTag(), formatJson(json), null);
        }
    }

    @Override
    public void xml(int priprity, @Nullable String xml) {
        if (allowLog) {
            log(priprity, getTag(), formatXml(xml), null);
        }
    }

    @Override
    public synchronized void printHttpResponseData(String url, Object params, String result, String json) {
        if (!allowLog) {
            return;
        }
        String tag = getTag();
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        for (int i = 3; i < sts.length; i++) {
            String className = sts[i].getClassName();
            if (!className.equals(LogUtils.class.getName()) && !className.equals(LogPrinterImpl.class.getName())) {
                String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
                if (tag == null) {
                    tag = simpleClassName;
                } else if (!tag.contains(simpleClassName)) {
                    tag = simpleClassName + "-" + tag;
                }
                break;
            }
        }
        String msgUrl = "URL:" + url;
        String msgParas = "requestParams:" + toString(params);
        String msgJson = formatJson(json);

        StringBuilder builder = new StringBuilder();
        builder.append(TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER + "\n")
                .append("Thread:" + Thread.currentThread().getName() + "\n")
                .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n")
                .append(msgUrl + "\n")
                .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n")
                .append(msgParas + "\n")
                .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n")
                .append(result + "\n")
                .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n")
                .append(msgJson);
        printMsg(Log.ERROR, tag, builder.toString());
    }

    @Override
    public void saveLogToSD(final String fileName, final String message) {
        ThreadUtil.start(new Runnable() {
            @Override
            public void run() {
                String logName = fileName;
                if (TextUtils.isEmpty(fileName)) {
                    logName = "DebugLog";
                }
                RandomAccessFile raf = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(new Date(System.currentTimeMillis()));
                    String date = time.substring(0, time.indexOf(" "));
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + LOG_FLODER_NAME + File.separatorChar + logName;
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File targetFile = new File(dir.getAbsolutePath() + File.separatorChar + date + ".log");
                        if (!targetFile.exists()) {
                            targetFile.createNewFile();

                        }
                        raf = new RandomAccessFile(targetFile, "rw");
                        raf.seek(targetFile.length());
                        if (targetFile.length() < 1) {
                            raf.write(DeviceUtil.getDeviceInfo().getBytes());
                        }
                        raf.writeBytes(System.getProperty("line.separator"));
                        raf.write(time.getBytes());
                        raf.writeBytes(System.getProperty("line.separator"));
                        raf.write(message.getBytes());
                        raf.close();
                        log(Log.ERROR, getTag(), "日志已保存到：" + targetFile.getAbsolutePath(), null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void saveErrorLogToSD(String errorMsg) {
        saveLogToSD("ErrorLog", errorMsg);
    }

    @Override
    public void saveThrowableToSD(Throwable throwable) {
        saveLogToSD("ErrorLog", Log.getStackTraceString(throwable));
    }

    @Override
    public void saveWarnLogToSD(String warnMsg) {
        saveLogToSD("WarnLog", warnMsg);
    }

    @Override
    public void saveDebugLogToSD(String debugMsg) {
        saveLogToSD("DebugLog", debugMsg);
    }

    private String formatJson(@Nullable String json) {
        if (TextUtils.isEmpty(json)) {
            return "这是一个空的json字符串";
        }
        json = json.trim();
        try {
            String jsonStr = null;
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                jsonStr = jsonObject.toString(4).replaceAll("\\\\/", "/");
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                jsonStr = jsonArray.toString(4).replaceAll("\\\\/", "/");
            }
            if (jsonStr != null) {
                return jsonStr;
            }
            return "json格式错误\n" + json;
        } catch (JSONException e) {
            e.printStackTrace();
            return "json格式错误\n" + json;
        }

    }

    private String formatXml(@Nullable String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "这是一个空的xml";
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            e.printStackTrace();
            return "xml打印出错啦\n" + xml;
        }
    }

    private synchronized void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (throwable != null && message != null) {
            message += "\n" + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = Log.getStackTraceString(throwable);
        }
        if (TextUtils.isEmpty(message)) {
            message = "日志为空";
        }

        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        boolean begin = false;
        int count = 0;
        int methodCount = getMethodCount();
        for (int i = 3; i < sts.length; i++) {
            String className = sts[i].getClassName();
            if (!begin && !className.equals(LogUtils.class.getName()) && !className.equals(LogPrinterImpl.class.getName())) {
                String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
                if (tag == null) {
                    tag = simpleClassName;
                } else if (!tag.contains(simpleClassName)) {
                    tag = simpleClassName + "-" + tag;
                }
                begin = true;
            }
            if (begin) {
                sb.append(className.substring(className.lastIndexOf(".") + 1))
                        .append(".")
                        .append(sts[i].getMethodName())
                        .append(" (")
                        .append(sts[i].getFileName())
                        .append(":")
                        .append(sts[i].getLineNumber())
                        .append(")\n");
                count++;
            }
            if (count >= methodCount) {
                count = 0;
                break;
            }
        }
        if (sb.toString().endsWith("\n")) {
            int index = sb.lastIndexOf("\n");
            sb.delete(index, sb.length());
        }
        StringBuilder builder = new StringBuilder();
        builder.append(TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER + "\n");
        if (getPrintThread()) {
            builder.append("Thread:" + Thread.currentThread().getName() + "\n")
                    .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n");
        }

        if (getPrintStack()) {
            builder.append(sb.toString() + "\n")
                    .append(SINGLE_DIVIDER + SINGLE_DIVIDER + "\n");
        }

        builder.append(message);
        printMsg(priority, tag, builder.toString());
    }

    private void printMsg(int priority, String tag, String message) {
        message = message.replaceAll("\n", "\n" + VERTICAL_LINE);
        message = message + "\n" + BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

        while (message.length() > 2048) {
            Log.println(priority, tag, " \n" + message.substring(0, 2049));
            message = VERTICAL_LINE + message.substring(2049);
        }
        Log.println(priority, tag, " \n" + message);
    }

    public String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }

    @Nullable
    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        return null;
    }

    /**
     * 获取调用轨迹，最少两层最多五层
     *
     * @return
     */
    private int getMethodCount() {
        Integer count = localMethodCount.get();
        if (count != null) {
            localMethodCount.remove();
        }
        if (count == null || count.intValue() < 2) {
            return 1;
        }
        if (count.intValue() > 5) {
            return 5;
        }
        return count.intValue();
    }

    /**
     * 是否打印当前线程，默认false
     *
     * @return
     */
    private boolean getPrintThread() {
        Boolean isPrint = localPrintThread.get();
        if (isPrint == null) {
            return false;
        }
        localPrintThread.remove();
        return isPrint.booleanValue();
    }

    /**
     * 是否打印调用堆栈，默认打印
     *
     * @return
     */
    private boolean getPrintStack() {
        Boolean isPrint = localPrintStack.get();
        if (isPrint == null) {
            return true;
        }
        localPrintStack.remove();
        return isPrint.booleanValue();
    }

    private String getMsg(String message, Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }
}
