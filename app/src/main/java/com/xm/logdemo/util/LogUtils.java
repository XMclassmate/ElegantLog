package com.xm.logdemo.util;

import android.support.annotation.Nullable;

/**
 * Created by XMclassmate on 2018/4/2.
 */

public final class LogUtils {

    private static LogPrinter logPrinter = LogPrinterImpl.newInstance();

    public static void init(boolean allowLog, @Nullable String sdLogFloderName) {
        logPrinter.setAllowLog(allowLog);
        logPrinter.setSDLogFloderName(sdLogFloderName);
    }

    /**
     * 设置tag
     *
     * @param tag
     * @return
     */
    public static LogPrinter tag(String tag) {
        return logPrinter.tag(tag);
    }

    /**
     * 设置是否打印线程信息，默认不打印
     *
     * @param isPrintThread
     * @return
     */
    public static LogPrinter printThread(boolean isPrintThread) {
        return logPrinter.printThread(isPrintThread);
    }

    /**
     * 设置是否打印方法调用树
     *
     * @param isPrintStack
     * @return
     */
    public static LogPrinter printStack(boolean isPrintStack) {
        return logPrinter.printStack(isPrintStack);
    }

    /**
     * 设置打印方法调用树，默认打印两层
     *
     * @param methodCount
     * @return
     */
    public static LogPrinter methodCount(int methodCount) {
        return logPrinter.methodCount(methodCount);
    }

    public static void d(String msg) {
        logPrinter.d(msg);
    }

    /**
     * 使用占位符的字符串打印
     *
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        logPrinter.d(msg, args);
    }

    public static void e(String msg) {
        logPrinter.e(msg);
    }

    public static void e(String msg, Object... args) {
        logPrinter.e(msg, args);
    }

    public static void e(Throwable throwable){
        logPrinter.e(throwable);
    }

    public static void e(Throwable throwable, String msg, Object... args) {
        logPrinter.e(throwable, msg, args);
    }

    public static void w(String msg) {
        logPrinter.w(msg);
    }

    public static void w(String msg, Object... args) {
        logPrinter.w(msg, args);
    }

    public static void i(String msg) {
        logPrinter.i(msg);
    }

    public static void i(String msg, Object... args) {
        logPrinter.i(msg, args);
    }

    public static void v(String msg) {
        logPrinter.v(msg);
    }

    public static void v(String msg, Object... args) {
        logPrinter.v(msg, args);
    }

    public static void wtf(String msg) {
        logPrinter.wtf(msg);
    }

    /**
     * assert日志，可用占位符
     *
     * @param msg
     * @param args
     */
    public static void wtf(String msg, Object... args) {
        logPrinter.wtf(msg, args);
    }

    /**
     * 打印json
     *
     * @param priority 日志级别，例如Log.DEBUG Log.ERROR
     * @param json
     */
    public static void json(int priority, String json) {
        logPrinter.json(priority, json);
    }

    /**
     * 打印xml
     *
     * @param priority 日志级别，例如Log.DEBUG Log.ERROR
     * @param xml
     */
    public static void xml(int priority, String xml) {
        logPrinter.xml(priority, xml);
    }

    /**
     * 打印接口数据
     *
     * @param url
     * @param params
     * @param result
     * @param json
     */
    public static void printHttpResponseData(String url, Object params, String result, String json) {
        logPrinter.printHttpResponseData(url, params, result, json);
    }

    /**
     * 保存日志到sd卡
     *
     * @param logName
     * @param msg
     */
    public static void saveLogToSD(String logName, String msg) {
        logPrinter.saveLogToSD(logName, msg);
    }

    public static void saveErrorLogToSD(String errorMsg) {
        logPrinter.saveErrorLogToSD(errorMsg);
    }

    public static void saveWarnLogToSD(String warnMsg) {
        logPrinter.saveWarnLogToSD(warnMsg);
    }

    public static void saveDebugLogToSD(String debugMsg) {
        logPrinter.saveDebugLogToSD(debugMsg);
    }

    public static void saveThrowableToSD(Throwable throwable) {
        logPrinter.saveThrowableToSD(throwable);
    }
}
