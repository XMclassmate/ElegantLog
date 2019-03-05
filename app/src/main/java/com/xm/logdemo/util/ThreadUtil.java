package com.xm.logdemo.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XMclassmate
 * @date 2018/1/29
 */

public class ThreadUtil {

    private static ThreadPoolExecutor threadPoolExecutor;
    /**
     * 通过线程池执行runable
     * @param runnable
     */
    public static void start(Runnable runnable){
        if (threadPoolExecutor == null){
            synchronized (ThreadUtil.class){
                if (threadPoolExecutor == null){
                    threadPoolExecutor = new ThreadPoolExecutor(3,5,1, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        threadPoolExecutor.execute(runnable);
    }
}
