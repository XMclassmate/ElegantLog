package com.xm.logdemo.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * log打印功能定义
 */
public interface LogPrinter {

    void setAllowLog(@Nullable boolean allowLog);

    LogPrinter tag(@NonNull String tag);

    LogPrinter printThread(@NonNull boolean isPrintThread);

    LogPrinter printStack(@NonNull boolean isPrintStack);

    LogPrinter methodCount(@NonNull int methodCount);

    void setSDLogFloderName(@Nullable String floderName);

    void d(@NonNull String message, @Nullable Object... args);

    void d(@Nullable Object object);

    void e(@NonNull String message, @Nullable Object... args);

    void e(@NonNull Throwable throwable);

    void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

    void w(@NonNull String message, @Nullable Object... args);

    void i(@NonNull String message, @Nullable Object... args);

    void v(@NonNull String message, @Nullable Object... args);

    void wtf(@NonNull String message, @Nullable Object... args);

    void json(int priority, @Nullable String json);

    void xml(int priority, @Nullable String xml);

    void printHttpResponseData(String url, Object params, String result, String json);

    void saveLogToSD(String fileName, String message);

    void saveErrorLogToSD(String errorMsg);

    void saveThrowableToSD(Throwable throwable);

    void saveWarnLogToSD(String warnMsg);

    void saveDebugLogToSD(String debugMsg);
}
